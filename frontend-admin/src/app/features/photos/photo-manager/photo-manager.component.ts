import { Component, DestroyRef, Input, OnInit, ViewChild, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HttpEventType } from '@angular/common/http';
import { finalize } from 'rxjs';
import { CdkDragDrop, DragDropModule, moveItemInArray } from '@angular/cdk/drag-drop';
import { ButtonModule } from 'primeng/button';
import { ConfirmDialog } from 'primeng/confirmdialog';
import { FileUpload } from 'primeng/fileupload';
import { Message } from 'primeng/message';
import { ProgressBar } from 'primeng/progressbar';
import { Skeleton } from 'primeng/skeleton';
import { TooltipModule } from 'primeng/tooltip';
import { ConfirmationService, MessageService } from 'primeng/api';
import { PropertyPhoto } from '@/app/core/models';
import { PhotoService } from '../services/photo.service';

const VALID_TYPES = ['image/jpeg', 'image/png', 'image/webp'];
const MAX_SIZE_BYTES = 10 * 1024 * 1024;
const COVER_COLOR = '#C9A84C';

@Component({
    selector: 'app-photo-manager',
    standalone: true,
    imports: [ButtonModule, ConfirmDialog, DragDropModule, FileUpload, Message, ProgressBar, Skeleton, TooltipModule],
    providers: [ConfirmationService],
    template: `
        <p-confirmdialog />

        <!-- Área de upload -->
        <p-fileupload
            #fileUploadRef
            [customUpload]="true"
            [multiple]="true"
            accept="image/jpeg,image/png,image/webp"
            [maxFileSize]="maxSizeBytes"
            [auto]="false"
            chooseLabel="Selecionar fotos"
            uploadLabel="Enviar fotos"
            cancelLabel="Cancelar"
            [disabled]="uploading()"
            (uploadHandler)="onUpload($event)"
            styleClass="w-full"
        >
            <ng-template #empty>
                <div class="flex flex-col items-center gap-2 py-6 text-muted-color">
                    <i class="pi pi-cloud-upload text-4xl"></i>
                    <span class="font-medium">Arraste fotos aqui ou clique em Selecionar</span>
                    <small>JPG, PNG, WEBP — máx. 10 MB por arquivo</small>
                </div>
            </ng-template>
        </p-fileupload>

        <!-- Erros de validação client-side -->
        @for (error of validationErrors(); track error) {
            <p-message severity="error" [text]="error" styleClass="w-full mt-2" />
        }

        <!-- Barra de progresso durante upload -->
        @if (uploading()) {
            <div class="mt-3 flex flex-col gap-1">
                <p-progressbar [value]="uploadProgress()" styleClass="w-full" [style]="{ height: '6px' }" />
                <span class="text-xs text-muted-color">Enviando fotos... {{ uploadProgress() }}%</span>
            </div>
        }

        <!-- Aviso: capa removida -->
        @if (deletedCoverWarning()) {
            <p-message severity="warn" text="A foto de capa foi removida. Defina uma nova capa para o imóvel." styleClass="w-full mt-3" />
        }

        <!-- Galeria -->
        <div class="mt-6">
            <div class="flex items-center justify-between mb-3">
                <span class="font-semibold text-sm text-muted-color uppercase tracking-wide"> Fotos cadastradas </span>
                @if (reordering()) {
                    <span class="text-xs text-muted-color flex items-center gap-1"> <i class="pi pi-spin pi-spinner"></i> Salvando ordem... </span>
                }
            </div>

            @if (loading()) {
                <div class="grid grid-cols-2 md:grid-cols-3 xl:grid-cols-4 gap-4">
                    @for (i of skeletonItems; track i) {
                        <p-skeleton height="10rem" borderRadius="8px" />
                    }
                </div>
            } @else if (photos().length === 0) {
                <p-message severity="info" text="Nenhuma foto cadastrada ainda." styleClass="w-full" />
            } @else {
                <div cdkDropList cdkDropListOrientation="mixed" [cdkDropListData]="photos()" (cdkDropListDropped)="onDrop($event)" class="grid grid-cols-2 md:grid-cols-3 xl:grid-cols-4 gap-4">
                    @for (photo of photos(); track photo.id) {
                        <div
                            cdkDrag
                            [cdkDragData]="photo"
                            class="relative rounded-lg overflow-hidden cursor-grab active:cursor-grabbing group"
                            [class.ring-2]="photo.cover"
                            [class.border]="!photo.cover"
                            [class.border-surface-200]="!photo.cover"
                            [style.outlineColor]="photo.cover ? coverColor : null"
                            [style.ringColor]="photo.cover ? coverColor : null"
                        >
                            <ng-template cdkDragPlaceholder>
                                <div class="h-40 rounded-lg bg-surface-100 dark:bg-surface-800 border-2 border-dashed border-surface-300 dark:border-surface-600"></div>
                            </ng-template>

                            <img [src]="photo.url" alt="Foto do imóvel" class="w-full h-40 object-cover" />

                            @if (photo.cover) {
                                <span class="absolute top-2 left-2 text-xs font-semibold px-2 py-0.5 rounded" [style.background]="coverColor" style="color: #fff">⭐ Capa</span>
                            }

                            <div class="absolute inset-0 bg-black/50 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center gap-2">
                                @if (!photo.cover) {
                                    <p-button icon="pi pi-star" size="small" pTooltip="Definir como capa" tooltipPosition="top" (onClick)="setCover(photo)" />
                                }
                                <p-button icon="pi pi-trash" severity="danger" size="small" pTooltip="Remover foto" tooltipPosition="top" (onClick)="confirmDelete(photo)" />
                            </div>
                        </div>
                    }
                </div>
            }
        </div>
    `
})
export class PhotoManagerComponent implements OnInit {
    @Input({ required: true }) propertyId!: string;
    @ViewChild('fileUploadRef') private fileUploadRef?: FileUpload;

    readonly photos = signal<PropertyPhoto[]>([]);
    readonly loading = signal(true);
    readonly uploading = signal(false);
    readonly uploadProgress = signal(0);
    readonly validationErrors = signal<string[]>([]);
    readonly reordering = signal(false);
    readonly deletedCoverWarning = signal(false);

    readonly skeletonItems = [0, 1, 2, 3];
    readonly maxSizeBytes = MAX_SIZE_BYTES;
    readonly coverColor = COVER_COLOR;

    private readonly photoService = inject(PhotoService);
    private readonly confirmationService = inject(ConfirmationService);
    private readonly messageService = inject(MessageService);
    private readonly destroyRef = inject(DestroyRef);

    ngOnInit(): void {
        this.photoService
            .findAll(this.propertyId)
            .pipe(
                finalize(() => this.loading.set(false)),
                takeUntilDestroyed(this.destroyRef)
            )
            .subscribe({ next: (r) => this.photos.set(r.data ?? []) });
    }

    onUpload(event: { files: File[] }): void {
        const errors = this.validateFiles(event.files);
        if (errors.length > 0) {
            this.validationErrors.set(errors);
            return;
        }

        this.validationErrors.set([]);
        this.uploading.set(true);
        this.uploadProgress.set(0);

        this.photoService
            .upload(this.propertyId, event.files)
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe({
                next: (httpEvent) => {
                    if (httpEvent.type === HttpEventType.UploadProgress) {
                        const total = httpEvent.total ?? 0;
                        this.uploadProgress.set(total > 0 ? Math.round((100 * httpEvent.loaded) / total) : 0);
                    } else if (httpEvent.type === HttpEventType.Response && httpEvent.body) {
                        this.photos.set(httpEvent.body.data ?? []);
                        this.deletedCoverWarning.set(false);
                        this.fileUploadRef?.clear();
                        this.uploading.set(false);
                        this.uploadProgress.set(0);
                    }
                },
                error: () => {
                    this.uploading.set(false);
                    this.uploadProgress.set(0);
                }
            });
    }

    onDrop(event: CdkDragDrop<PropertyPhoto[]>): void {
        if (event.previousIndex === event.currentIndex) return;

        const previous = [...this.photos()];
        const updated = [...this.photos()];
        moveItemInArray(updated, event.previousIndex, event.currentIndex);

        this.photos.set(updated);
        this.reordering.set(true);

        const order = updated.map((p, i) => ({ id: p.id, orderIndex: i + 1 }));

        this.photoService
            .reorder(this.propertyId, order)
            .pipe(
                finalize(() => this.reordering.set(false)),
                takeUntilDestroyed(this.destroyRef)
            )
            .subscribe({
                next: (r) => this.photos.set(r.data ?? []),
                error: () => this.photos.set(previous)
            });
    }

    setCover(photo: PropertyPhoto): void {
        const previous = [...this.photos()];

        this.photos.update((ps) => ps.map((p) => ({ ...p, cover: p.id === photo.id })));

        this.photoService
            .setCover(this.propertyId, photo.id)
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe({
                next: (r) => {
                    this.photos.set(r.data ?? []);
                    this.deletedCoverWarning.set(false);
                },
                error: () => this.photos.set(previous)
            });
    }

    confirmDelete(photo: PropertyPhoto): void {
        this.confirmationService.confirm({
            message: 'Deseja remover esta foto? Esta ação não pode ser desfeita.',
            header: 'Remover foto',
            icon: 'pi pi-exclamation-triangle',
            acceptLabel: 'Remover',
            rejectLabel: 'Cancelar',
            acceptButtonStyleClass: 'p-button-danger',
            accept: () => this.deletePhoto(photo)
        });
    }

    private deletePhoto(photo: PropertyPhoto): void {
        const wasCover = photo.cover;

        this.photoService
            .delete(this.propertyId, photo.id)
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe({
                next: () => {
                    this.photos.update((ps) => ps.filter((p) => p.id !== photo.id));
                    this.messageService.add({ severity: 'success', summary: 'Sucesso', detail: 'Foto removida com sucesso.' });
                    if (wasCover) {
                        this.deletedCoverWarning.set(true);
                    }
                }
            });
    }

    private validateFiles(files: File[]): string[] {
        const errors: string[] = [];
        for (const file of files) {
            if (file.size > MAX_SIZE_BYTES) {
                errors.push(`O arquivo "${file.name}" excede o tamanho máximo de 10MB.`);
            } else if (!VALID_TYPES.includes(file.type)) {
                errors.push(`O arquivo "${file.name}" não é uma imagem válida.`);
            }
        }
        return errors;
    }
}
