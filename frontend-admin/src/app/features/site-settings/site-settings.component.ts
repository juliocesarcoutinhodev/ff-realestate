import { Component, DestroyRef, OnInit, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { ButtonModule } from 'primeng/button';
import { Breadcrumb } from 'primeng/breadcrumb';
import { Card } from 'primeng/card';
import { InputTextModule } from 'primeng/inputtext';
import { Skeleton } from 'primeng/skeleton';
import { Tabs, TabList, Tab, TabPanels, TabPanel } from 'primeng/tabs';
import { Textarea } from 'primeng/textarea';
import { MenuItem } from 'primeng/api';
import { SiteSettings, SiteSettingsForm } from '@/app/core/models';
import { SiteSettingsImagesComponent } from './site-settings-images/site-settings-images.component';
import { SiteSettingsService } from './services/site-settings.service';

@Component({
    selector: 'app-site-settings',
    standalone: true,
    imports: [
        ReactiveFormsModule,
        ButtonModule,
        InputTextModule,
        Breadcrumb,
        Card,
        Skeleton,
        Tabs,
        TabList,
        Tab,
        TabPanels,
        TabPanel,
        Textarea,
        SiteSettingsImagesComponent
    ],
    template: `
        <div class="flex flex-col gap-4 pb-6">
            <p-breadcrumb [model]="breadcrumbItems" [home]="breadcrumbHome" />

            <p-tabs [value]="activeTab()">
                <p-tablist>
                    <p-tab value="dados" (click)="activeTab.set('dados')">Dados</p-tab>
                    <p-tab value="imagens" (click)="activeTab.set('imagens')">Imagens</p-tab>
                </p-tablist>
                <p-tabpanels>
                    <p-tabpanel value="dados">
                        @if (loading()) {
                            <div class="flex flex-col gap-4">
                                <p-card>
                                    <div class="flex flex-col gap-3">
                                        <p-skeleton height="2rem" styleClass="w-full" />
                                        <p-skeleton height="2rem" styleClass="w-full" />
                                        <p-skeleton height="5rem" styleClass="w-full" />
                                    </div>
                                </p-card>
                                <p-card>
                                    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                                        <p-skeleton height="2rem" />
                                        <p-skeleton height="2rem" />
                                        <p-skeleton height="2rem" />
                                        <p-skeleton height="2rem" />
                                    </div>
                                </p-card>
                                <p-card>
                                    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                                        <p-skeleton height="2rem" />
                                        <p-skeleton height="2rem" />
                                        <p-skeleton height="2rem" />
                                    </div>
                                </p-card>
                                <p-card>
                                    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                                        <p-skeleton height="2rem" />
                                        <p-skeleton height="2rem" />
                                        <p-skeleton height="2rem" />
                                    </div>
                                </p-card>
                                <p-card>
                                    <p-skeleton height="2rem" styleClass="w-full" />
                                </p-card>
                            </div>
                        } @else {
                            <form [formGroup]="form" (ngSubmit)="save()" class="flex flex-col gap-4">
                                <!-- Seção 1: Corretor -->
                                <p-card header="Corretor">
                                    <div class="flex flex-col gap-4">
                                        <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                                            <div class="flex flex-col gap-2">
                                                <label class="font-medium text-sm">Nome <span class="text-red-500">*</span></label>
                                                <input pInputText formControlName="brokerName" placeholder="Nome completo do corretor" class="w-full" />
                                                @if (form.get('brokerName')?.errors?.['required'] && form.get('brokerName')?.touched) {
                                                    <small class="text-red-500">Nome é obrigatório.</small>
                                                }
                                            </div>
                                            <div class="flex flex-col gap-2">
                                                <label class="font-medium text-sm">CRECI <span class="text-red-500">*</span></label>
                                                <input pInputText formControlName="brokerCreci" placeholder="Ex: 123456-F" class="w-full" />
                                                @if (form.get('brokerCreci')?.errors?.['required'] && form.get('brokerCreci')?.touched) {
                                                    <small class="text-red-500">CRECI é obrigatório.</small>
                                                }
                                            </div>
                                        </div>
                                        <div class="flex flex-col gap-2">
                                            <label class="font-medium text-sm">Bio</label>
                                            <textarea pTextarea formControlName="brokerBio" [autoResize]="true" rows="4" placeholder="Breve apresentação do corretor..." class="w-full"></textarea>
                                        </div>
                                    </div>
                                </p-card>

                                <!-- Seção 2: Hero (Banner Principal) -->
                                <p-card header="Banner Principal">
                                    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                                        <div class="flex flex-col gap-2">
                                            <label class="font-medium text-sm">Título</label>
                                            <input pInputText formControlName="heroTitle" placeholder="Ex: Encontre o imóvel dos seus sonhos" class="w-full" />
                                        </div>
                                        <div class="flex flex-col gap-2">
                                            <label class="font-medium text-sm">Subtítulo</label>
                                            <input pInputText formControlName="heroSubtitle" placeholder="Ex: Especialista em imóveis residenciais" class="w-full" />
                                        </div>
                                    </div>
                                </p-card>

                                <!-- Seção 3: Contato -->
                                <p-card header="Contato">
                                    <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-4">
                                        <div class="flex flex-col gap-2">
                                            <label class="font-medium text-sm">Telefone</label>
                                            <input pInputText formControlName="phone" placeholder="Ex: 5511999999999" class="w-full" />
                                        </div>
                                        <div class="flex flex-col gap-2">
                                            <label class="font-medium text-sm">WhatsApp <span class="text-red-500">*</span></label>
                                            <input pInputText formControlName="whatsapp" placeholder="Ex: 5511999999999" class="w-full" />
                                            @if (form.get('whatsapp')?.errors?.['required'] && form.get('whatsapp')?.touched) {
                                                <small class="text-red-500">WhatsApp é obrigatório.</small>
                                            }
                                            @if (form.get('whatsapp')?.errors?.['pattern'] && form.get('whatsapp')?.touched) {
                                                <small class="text-red-500">WhatsApp deve conter apenas dígitos, com mínimo de 10 caracteres.</small>
                                            }
                                        </div>
                                        <div class="flex flex-col gap-2">
                                            <label class="font-medium text-sm">E-mail</label>
                                            <input pInputText formControlName="email" placeholder="Ex: corretor@email.com" type="email" class="w-full" />
                                            @if (form.get('email')?.errors?.['email'] && form.get('email')?.touched) {
                                                <small class="text-red-500">E-mail inválido.</small>
                                            }
                                        </div>
                                        <div class="flex flex-col gap-2 md:col-span-2 xl:col-span-3">
                                            <label class="font-medium text-sm">Mensagem padrão WhatsApp</label>
                                            <input pInputText formControlName="whatsappMessage" placeholder="Ex: Olá! Tenho interesse em seus imóveis." class="w-full" />
                                        </div>
                                    </div>
                                </p-card>

                                <!-- Seção 4: Redes Sociais -->
                                <p-card header="Redes Sociais">
                                    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                                        <div class="flex flex-col gap-2">
                                            <label class="font-medium text-sm">Instagram</label>
                                            <input pInputText formControlName="instagramUrl" placeholder="https://instagram.com/..." class="w-full" />
                                        </div>
                                        <div class="flex flex-col gap-2">
                                            <label class="font-medium text-sm">Facebook</label>
                                            <input pInputText formControlName="facebookUrl" placeholder="https://facebook.com/..." class="w-full" />
                                        </div>
                                        <div class="flex flex-col gap-2">
                                            <label class="font-medium text-sm">LinkedIn</label>
                                            <input pInputText formControlName="linkedinUrl" placeholder="https://linkedin.com/in/..." class="w-full" />
                                        </div>
                                    </div>
                                </p-card>

                                <!-- Seção 5: SEO -->
                                <p-card header="SEO">
                                    <div class="flex flex-col gap-2">
                                        <label class="font-medium text-sm">Meta Description</label>
                                        <input pInputText formControlName="metaDescription" placeholder="Breve descrição para mecanismos de busca (até 160 caracteres)" maxlength="160" class="w-full" />
                                    </div>
                                </p-card>

                                <!-- Rodapé fixo -->
                                <div class="sticky bottom-0 bg-surface-0 dark:bg-surface-900 border-t border-surface-200 dark:border-surface-700 -mx-2 px-6 py-4 flex justify-end gap-3 mt-2">
                                    <p-button type="submit" label="Salvar" icon="pi pi-check" [loading]="loadingForm()" [disabled]="form.invalid" />
                                </div>
                            </form>
                        }
                    </p-tabpanel>
                    <p-tabpanel value="imagens">
                        @if (settings()) {
                            <app-site-settings-images [settings]="settings()!" />
                        }
                    </p-tabpanel>
                </p-tabpanels>
            </p-tabs>
        </div>
    `
})
export class SiteSettingsComponent implements OnInit {
    private readonly fb = inject(FormBuilder);
    private readonly siteSettingsService = inject(SiteSettingsService);
    private readonly destroyRef = inject(DestroyRef);

    readonly loading = signal(true);
    readonly loadingForm = signal(false);
    readonly activeTab = signal('dados');
    readonly settings = signal<SiteSettings | null>(null);

    readonly breadcrumbItems: MenuItem[] = [{ label: 'Configurações do Site' }];
    readonly breadcrumbHome: MenuItem = { icon: 'pi pi-home', routerLink: '/dashboard' };

    readonly form = this.fb.group({
        brokerName: ['', Validators.required],
        brokerCreci: ['', Validators.required],
        brokerBio: [''],
        heroTitle: [''],
        heroSubtitle: [''],
        phone: [''],
        whatsapp: ['', [Validators.required, Validators.pattern(/^\d{10,}$/)]],
        whatsappMessage: [''],
        email: ['', Validators.email],
        instagramUrl: [''],
        facebookUrl: [''],
        linkedinUrl: [''],
        metaDescription: ['']
    });

    ngOnInit(): void {
        this.siteSettingsService
            .get()
            .pipe(
                finalize(() => this.loading.set(false)),
                takeUntilDestroyed(this.destroyRef)
            )
            .subscribe({
                next: (r) => {
                    this.settings.set(r.data);
                    this.patchForm(r.data);
                },
                error: () => {}
            });
    }

    private patchForm(s: SiteSettings): void {
        this.form.patchValue({
            brokerName: s.brokerName,
            brokerCreci: s.brokerCreci,
            brokerBio: s.brokerBio ?? '',
            heroTitle: s.heroTitle ?? '',
            heroSubtitle: s.heroSubtitle ?? '',
            phone: s.phone ?? '',
            whatsapp: s.whatsapp,
            whatsappMessage: s.whatsappMessage ?? '',
            email: s.email ?? '',
            instagramUrl: s.instagramUrl ?? '',
            facebookUrl: s.facebookUrl ?? '',
            linkedinUrl: s.linkedinUrl ?? '',
            metaDescription: s.metaDescription ?? ''
        });
    }

    save(): void {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            return;
        }
        this.loadingForm.set(true);
        const payload = this.form.getRawValue() as unknown as SiteSettingsForm;

        this.siteSettingsService
            .update(payload)
            .pipe(
                finalize(() => this.loadingForm.set(false)),
                takeUntilDestroyed(this.destroyRef)
            )
            .subscribe({
                next: () => this.activeTab.set('imagens'),
                error: () => {}
            });
    }
}
