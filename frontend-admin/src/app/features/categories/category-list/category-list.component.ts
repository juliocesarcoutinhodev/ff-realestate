import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { map } from 'rxjs';
import { ButtonModule } from 'primeng/button';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { Dialog } from 'primeng/dialog';
import { IconField } from 'primeng/iconfield';
import { InputIcon } from 'primeng/inputicon';
import { InputTextModule } from 'primeng/inputtext';
import { SkeletonModule } from 'primeng/skeleton';
import { TableModule } from 'primeng/table';
import { TooltipModule } from 'primeng/tooltip';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Category } from '@/app/core/models';
import { CategoryService } from '../services/category.service';
import { CategoryModalComponent } from '../category-modal/category-modal.component';

@Component({
    selector: 'app-category-list',
    standalone: true,
    imports: [
        TableModule,
        ButtonModule,
        InputTextModule,
        TooltipModule,
        SkeletonModule,
        Dialog,
        ConfirmDialog,
        IconField,
        InputIcon,
        CategoryModalComponent
    ],
    providers: [ConfirmationService],
    template: `
        <p-confirmdialog />

        <app-category-modal
            [category]="selectedCategory()"
            [visible]="modalVisible()"
            (visibleChange)="modalVisible.set($event)"
            (saved)="onSaved()"
        />

        <div class="card">
            <div class="flex items-center justify-between mb-6">
                <span class="text-xl font-semibold">Categorias</span>
                <p-button label="Nova Categoria" icon="pi pi-plus" (onClick)="openCreate()" />
            </div>

            <div class="mb-4">
                <p-iconfield>
                    <p-inputicon styleClass="pi pi-search" />
                    <input
                        pInputText
                        [value]="searchValue()"
                        (input)="searchValue.set($any($event.target).value)"
                        placeholder="Buscar por nome..."
                        class="w-full"
                    />
                </p-iconfield>
            </div>

            <p-table [value]="filteredCategories()" [loading]="loading()" responsiveLayout="scroll">
                <ng-template #loadingbody>
                    @for (_ of skeletonRows; track _) {
                        <tr>
                            <td><p-skeleton height="1.5rem" /></td>
                            <td><p-skeleton height="1.5rem" /></td>
                            <td><p-skeleton height="1.5rem" /></td>
                            <td class="flex justify-center gap-2">
                                <p-skeleton width="2rem" height="2rem" borderRadius="50%" />
                                <p-skeleton width="2rem" height="2rem" borderRadius="50%" />
                            </td>
                        </tr>
                    }
                </ng-template>

                <ng-template #header>
                    <tr>
                        <th style="min-width:12rem">Nome</th>
                        <th style="min-width:10rem">Slug</th>
                        <th>Descrição</th>
                        <th style="width:7rem" class="text-center">Ações</th>
                    </tr>
                </ng-template>

                <ng-template #body let-category>
                    <tr class="cursor-pointer hover:bg-surface-50" (click)="openView(category)">
                        <td class="font-medium">{{ category.name }}</td>
                        <td>
                            <code class="text-xs bg-surface-100 dark:bg-surface-800 px-2 py-1 rounded">
                                {{ category.slug }}
                            </code>
                        </td>
                        <td class="text-muted-color text-sm">{{ category.description || '—' }}</td>
                        <td>
                            <div class="flex gap-1 justify-center">
                                <p-button
                                    icon="pi pi-pencil"
                                    [text]="true"
                                    severity="secondary"
                                    size="small"
                                    pTooltip="Editar"
                                    tooltipPosition="top"
                                    (onClick)="openEdit(category, $event)"
                                />
                                <p-button
                                    icon="pi pi-trash"
                                    [text]="true"
                                    severity="danger"
                                    size="small"
                                    pTooltip="Remover"
                                    tooltipPosition="top"
                                    (onClick)="confirmDelete(category, $event)"
                                />
                            </div>
                        </td>
                    </tr>
                </ng-template>

                <ng-template #emptymessage>
                    <tr>
                        <td colspan="4" class="text-center py-12">
                            <div class="flex flex-col items-center gap-3 text-muted-color">
                                <i class="pi pi-tags text-4xl"></i>
                                <span>Nenhuma categoria cadastrada ainda.</span>
                            </div>
                        </td>
                    </tr>
                </ng-template>
            </p-table>
        </div>

        <!-- Modal: Visualizar (read-only) -->
        <p-dialog
            header="Detalhes da Categoria"
            [visible]="viewVisible()"
            (visibleChange)="viewVisible.set($event)"
            [modal]="true"
            [style]="{ width: '28rem' }"
            [draggable]="false"
        >
            @if (selectedCategory(); as cat) {
                <div class="flex flex-col gap-5 py-2">
                    <div>
                        <span class="block text-muted-color text-xs uppercase tracking-wide mb-1">Nome</span>
                        <span class="font-semibold text-lg">{{ cat.name }}</span>
                    </div>
                    <div>
                        <span class="block text-muted-color text-xs uppercase tracking-wide mb-1">Slug</span>
                        <code class="bg-surface-100 dark:bg-surface-800 px-2 py-1 rounded text-sm">{{ cat.slug }}</code>
                    </div>
                    <div>
                        <span class="block text-muted-color text-xs uppercase tracking-wide mb-1">Descrição</span>
                        <span class="text-sm">{{ cat.description || 'Sem descrição.' }}</span>
                    </div>
                </div>
            }

            <ng-template #footer>
                <div class="flex justify-end gap-2">
                    <p-button
                        label="Fechar"
                        severity="secondary"
                        [text]="true"
                        (onClick)="viewVisible.set(false)"
                    />
                    <p-button
                        label="Editar"
                        icon="pi pi-pencil"
                        (onClick)="editFromView()"
                    />
                </div>
            </ng-template>
        </p-dialog>
    `
})
export class CategoryListComponent implements OnInit {
    private readonly categoryService = inject(CategoryService);
    private readonly confirmationService = inject(ConfirmationService);
    private readonly messageService = inject(MessageService);

    readonly skeletonRows = [1, 2, 3, 4, 5];

    readonly loading = signal(true);
    readonly categories = signal<Category[]>([]);
    readonly searchValue = signal('');
    readonly selectedCategory = signal<Category | undefined>(undefined);
    readonly modalVisible = signal(false);
    readonly viewVisible = signal(false);

    readonly filteredCategories = computed(() => {
        const q = this.searchValue().toLowerCase().trim();
        if (!q) return this.categories();
        return this.categories().filter((c) => c.name.toLowerCase().includes(q));
    });

    ngOnInit(): void {
        this.loadCategories();
    }

    openCreate(): void {
        this.selectedCategory.set(undefined);
        this.modalVisible.set(true);
    }

    openEdit(category: Category, event: MouseEvent): void {
        event.stopPropagation();
        this.selectedCategory.set(category);
        this.modalVisible.set(true);
    }

    openView(category: Category): void {
        this.selectedCategory.set(category);
        this.viewVisible.set(true);
    }

    editFromView(): void {
        this.viewVisible.set(false);
        this.modalVisible.set(true);
    }

    onSaved(): void {
        this.loadCategories();
    }

    confirmDelete(category: Category, event: MouseEvent): void {
        event.stopPropagation();
        this.confirmationService.confirm({
            message: `Deseja excluir a categoria "${category.name}"? Esta ação não pode ser desfeita.`,
            header: 'Confirmar exclusão',
            icon: 'pi pi-exclamation-triangle',
            acceptLabel: 'Excluir',
            rejectLabel: 'Cancelar',
            acceptButtonStyleClass: 'p-button-danger',
            accept: () => this.deleteCategory(category.id)
        });
    }

    private loadCategories(): void {
        this.loading.set(true);
        this.categoryService
            .findAll()
            .pipe(map((r) => r.data))
            .subscribe({
                next: (data) => {
                    this.categories.set(data);
                    this.loading.set(false);
                }
            });
    }

    private deleteCategory(id: string): void {
        this.categoryService.delete(id).subscribe({
            next: () => {
                this.categories.update((list) => list.filter((c) => c.id !== id));
                this.messageService.add({ severity: 'success', summary: 'Sucesso', detail: 'Categoria excluída com sucesso.' });
            }
        });
    }
}
