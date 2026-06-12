import { Component, DestroyRef, OnInit, ViewChild, inject, input, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { finalize } from 'rxjs';
import { Avatar } from 'primeng/avatar';
import { Card } from 'primeng/card';
import { FileUpload } from 'primeng/fileupload';
import { ProgressSpinner } from 'primeng/progressspinner';
import { SiteSettings } from '@/app/core/models';
import { SiteSettingsService } from '../services/site-settings.service';

const MAX_SIZE_BYTES = 5 * 1024 * 1024;

@Component({
    selector: 'app-site-settings-images',
    standalone: true,
    imports: [Avatar, Card, FileUpload, ProgressSpinner],
    template: `
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6 mt-4">
            <!-- Card 1: Foto do Corretor -->
            <p-card header="Foto do Corretor">
                <div class="flex flex-col items-center gap-4">
                    <div class="relative flex items-center justify-center w-36 h-36">
                        @if (loadingPhoto()) {
                            <p-progressspinner [style]="{ width: '3rem', height: '3rem' }" />
                        } @else if (photoUrl()) {
                            <img [src]="photoUrl()" alt="Foto do corretor" class="w-36 h-36 rounded-full object-cover border-2 border-surface-200" />
                        } @else {
                            <p-avatar icon="pi pi-user" size="xlarge" shape="circle" styleClass="w-36 h-36 !text-4xl" />
                        }
                    </div>

                    <p-fileupload
                        #photoUploadRef
                        [customUpload]="true"
                        [auto]="true"
                        [multiple]="false"
                        accept="image/jpeg,image/png,image/webp"
                        [maxFileSize]="maxSizeBytes"
                        [disabled]="loadingPhoto()"
                        chooseLabel="Selecionar foto"
                        (uploadHandler)="onUploadPhoto($event)"
                        styleClass="w-full"
                    >
                        <ng-template #empty>
                            <div class="flex flex-col items-center gap-2 py-4 text-muted-color">
                                <i class="pi pi-cloud-upload text-3xl"></i>
                                <span class="text-sm font-medium">Arraste a foto aqui ou clique em Selecionar</span>
                                <small>JPG, PNG, WEBP — máx. 5 MB</small>
                            </div>
                        </ng-template>
                    </p-fileupload>
                </div>
            </p-card>

            <!-- Card 2: Imagem Hero (Banner) -->
            <p-card header="Imagem Hero (Banner)">
                <div class="flex flex-col gap-4">
                    <div class="relative w-full h-36 rounded-lg overflow-hidden bg-surface-100 dark:bg-surface-800 flex items-center justify-center">
                        @if (loadingHero()) {
                            <p-progressspinner [style]="{ width: '3rem', height: '3rem' }" />
                        } @else if (heroUrl()) {
                            <img [src]="heroUrl()" alt="Imagem hero" class="w-full h-full object-cover" />
                        } @else {
                            <div class="flex flex-col items-center gap-2 text-muted-color">
                                <i class="pi pi-image text-4xl"></i>
                                <small>Sem imagem</small>
                            </div>
                        }
                    </div>

                    <p-fileupload
                        #heroUploadRef
                        [customUpload]="true"
                        [auto]="true"
                        [multiple]="false"
                        accept="image/jpeg,image/png,image/webp"
                        [maxFileSize]="maxSizeBytes"
                        [disabled]="loadingHero()"
                        chooseLabel="Selecionar imagem"
                        (uploadHandler)="onUploadHero($event)"
                        styleClass="w-full"
                    >
                        <ng-template #empty>
                            <div class="flex flex-col items-center gap-2 py-4 text-muted-color">
                                <i class="pi pi-cloud-upload text-3xl"></i>
                                <span class="text-sm font-medium">Arraste a imagem aqui ou clique em Selecionar</span>
                                <small>JPG, PNG, WEBP — máx. 5 MB</small>
                            </div>
                        </ng-template>
                    </p-fileupload>
                </div>
            </p-card>
        </div>
    `
})
export class SiteSettingsImagesComponent implements OnInit {
    @ViewChild('photoUploadRef') private photoUploadRef?: FileUpload;
    @ViewChild('heroUploadRef') private heroUploadRef?: FileUpload;

    readonly settings = input.required<SiteSettings>();

    readonly loadingPhoto = signal(false);
    readonly loadingHero = signal(false);
    readonly photoUrl = signal<string | undefined>(undefined);
    readonly heroUrl = signal<string | undefined>(undefined);

    readonly maxSizeBytes = MAX_SIZE_BYTES;

    private readonly siteSettingsService = inject(SiteSettingsService);
    private readonly destroyRef = inject(DestroyRef);

    ngOnInit(): void {
        const s = this.settings();
        this.photoUrl.set(s.brokerPhotoUrl);
        this.heroUrl.set(s.heroImageUrl);
    }

    onUploadPhoto(event: { files: File[] }): void {
        const file = event.files[0];
        if (!file) return;
        this.loadingPhoto.set(true);
        this.siteSettingsService
            .uploadBrokerPhoto(file)
            .pipe(
                finalize(() => this.loadingPhoto.set(false)),
                takeUntilDestroyed(this.destroyRef)
            )
            .subscribe({
                next: (r) => {
                    this.photoUrl.set(r.data.url);
                    this.photoUploadRef?.clear();
                },
                error: () => this.photoUploadRef?.clear()
            });
    }

    onUploadHero(event: { files: File[] }): void {
        const file = event.files[0];
        if (!file) return;
        this.loadingHero.set(true);
        this.siteSettingsService
            .uploadHeroImage(file)
            .pipe(
                finalize(() => this.loadingHero.set(false)),
                takeUntilDestroyed(this.destroyRef)
            )
            .subscribe({
                next: (r) => {
                    this.heroUrl.set(r.data.url);
                    this.heroUploadRef?.clear();
                },
                error: () => this.heroUploadRef?.clear()
            });
    }
}
