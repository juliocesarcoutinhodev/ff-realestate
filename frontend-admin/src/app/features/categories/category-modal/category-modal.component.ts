import { Component, computed, effect, EventEmitter, inject, Input, model, Output, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { toSignal } from '@angular/core/rxjs-interop';
import { finalize } from 'rxjs';
import { startWith } from 'rxjs/operators';
import { ButtonModule } from 'primeng/button';
import { Dialog } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { Textarea } from 'primeng/textarea';
import { Category, CategoryForm } from '@/app/core/models';
import { CategoryService } from '../services/category.service';

@Component({
    selector: 'app-category-modal',
    standalone: true,
    imports: [ReactiveFormsModule, ButtonModule, InputTextModule, Textarea, Dialog],
    template: `
        <p-dialog
            [header]="category ? 'Editar Categoria' : 'Nova Categoria'"
            [visible]="visible()"
            (visibleChange)="visible.set($event)"
            [modal]="true"
            [style]="{ width: '32rem' }"
            [draggable]="false"
        >
            <form [formGroup]="form" (ngSubmit)="save()" class="flex flex-col gap-5 pt-2">
                <div class="flex flex-col gap-2">
                    <label class="font-medium text-sm">
                        Nome <span class="text-red-500">*</span>
                    </label>
                    <input
                        pInputText
                        formControlName="name"
                        placeholder="Ex: Apartamentos"
                        class="w-full"
                    />
                    @if (form.get('name')?.errors?.['required'] && form.get('name')?.touched) {
                        <small class="text-red-500">Nome é obrigatório.</small>
                    } @else if (form.get('name')?.errors?.['minlength'] && form.get('name')?.touched) {
                        <small class="text-red-500">Nome deve ter pelo menos 2 caracteres.</small>
                    } @else if (form.get('name')?.errors?.['maxlength'] && form.get('name')?.touched) {
                        <small class="text-red-500">Nome deve ter no máximo 100 caracteres.</small>
                    }
                    @if (slugPreview()) {
                        <small class="text-muted-color">
                            Slug: <code class="bg-surface-100 dark:bg-surface-800 px-1.5 py-0.5 rounded text-xs">{{ slugPreview() }}</code>
                        </small>
                    }
                </div>
                <div class="flex flex-col gap-2">
                    <label class="font-medium text-sm">Descrição</label>
                    <textarea
                        pTextarea
                        formControlName="description"
                        rows="3"
                        placeholder="Descrição opcional da categoria"
                        class="w-full"
                    ></textarea>
                    @if (form.get('description')?.errors?.['maxlength'] && form.get('description')?.touched) {
                        <small class="text-red-500">Descrição deve ter no máximo 500 caracteres.</small>
                    }
                </div>
            </form>

            <ng-template #footer>
                <div class="flex justify-end gap-2">
                    <p-button
                        label="Cancelar"
                        severity="secondary"
                        [text]="true"
                        (onClick)="cancel()"
                    />
                    <p-button
                        [label]="category ? 'Salvar' : 'Criar'"
                        [loading]="loading()"
                        [disabled]="form.invalid"
                        (onClick)="save()"
                    />
                </div>
            </ng-template>
        </p-dialog>
    `
})
export class CategoryModalComponent {
    private readonly categoryService = inject(CategoryService);
    private readonly fb = inject(FormBuilder);

    @Input() category?: Category;
    @Output() saved = new EventEmitter<void>();

    readonly visible = model(false);
    readonly loading = signal(false);

    readonly form = this.fb.group({
        name: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
        description: ['', Validators.maxLength(500)]
    });

    private readonly nameValue = toSignal(
        this.form.get('name')!.valueChanges.pipe(startWith('')),
        { initialValue: '' }
    );

    readonly slugPreview = computed(() => this.toSlug(this.nameValue() ?? ''));

    constructor() {
        effect(() => {
            if (this.visible()) {
                this.setupForm();
            }
        });
    }

    save(): void {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            return;
        }
        this.loading.set(true);
        const payload = this.form.getRawValue() as CategoryForm;
        const isEdit = !!this.category;

        const request$ = isEdit
            ? this.categoryService.update(this.category!.id, payload)
            : this.categoryService.create(payload);

        request$.pipe(finalize(() => this.loading.set(false))).subscribe({
            next: () => {
                this.visible.set(false);
                this.saved.emit();
            }
        });
    }

    cancel(): void {
        this.visible.set(false);
    }

    private setupForm(): void {
        if (this.category) {
            this.form.patchValue({
                name: this.category.name,
                description: this.category.description ?? ''
            });
        } else {
            this.form.reset();
        }
    }

    private toSlug(name: string): string {
        return name
            .toLowerCase()
            .trim()
            .normalize('NFD')
            .replace(/[̀-ͯ]/g, '')
            .replace(/[^a-z0-9\s-]/g, '')
            .replace(/\s+/g, '-')
            .replace(/-{2,}/g, '-');
    }
}
