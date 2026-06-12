import { Component, DestroyRef, Input, OnInit, computed, inject, signal } from '@angular/core';
import { NgTemplateOutlet } from '@angular/common';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpContext } from '@angular/common/http';
import { Router } from '@angular/router';
import { map, finalize } from 'rxjs';
import { ButtonModule } from 'primeng/button';
import { Breadcrumb } from 'primeng/breadcrumb';
import { Card } from 'primeng/card';
import { InputNumber } from 'primeng/inputnumber';
import { InputTextModule } from 'primeng/inputtext';
import { Message } from 'primeng/message';
import { ProgressSpinner } from 'primeng/progressspinner';
import { Select } from 'primeng/select';
import { SelectButton } from 'primeng/selectbutton';
import { Skeleton } from 'primeng/skeleton';
import { Tabs, TabList, Tab, TabPanels, TabPanel } from 'primeng/tabs';
import { Textarea } from 'primeng/textarea';
import { ToggleButton } from 'primeng/togglebutton';
import { MenuItem } from 'primeng/api';
import { SKIP_ERROR_TOAST } from '@/app/core/interceptors/error.interceptor';
import { PropertyDetail, PropertyForm } from '@/app/core/models';
import { CategoryService } from '@/app/features/categories/services/category.service';
import { PhotoManagerComponent } from '@/app/features/photos/photo-manager/photo-manager.component';
import { PropertyService } from '../services/property.service';
import { ZipCodeService } from '../services/zip-code.service';

@Component({
    selector: 'app-property-form',
    standalone: true,
    imports: [
        NgTemplateOutlet, ReactiveFormsModule,
        ButtonModule, InputTextModule,
        Breadcrumb, Card, InputNumber, Message, ProgressSpinner, Select,
        SelectButton, Skeleton, Tabs, TabList, Tab, TabPanels, TabPanel,
        Textarea, ToggleButton,
        PhotoManagerComponent
    ],
    template: `
        <div class="flex flex-col gap-4 pb-6">
            <p-breadcrumb [model]="breadcrumbItems()" [home]="breadcrumbHome" />

            @if (isEditMode) {
                <!-- Modo edição: tabs Dados / Fotos -->
                <p-tabs [value]="activeTab()">
                    <p-tablist>
                        <p-tab value="dados" (click)="activeTab.set('dados')">Dados</p-tab>
                        <p-tab value="fotos" (click)="activeTab.set('fotos')">Fotos</p-tab>
                    </p-tablist>
                    <p-tabpanels>
                        <p-tabpanel value="dados">
                            @if (loading()) {
                                <ng-container *ngTemplateOutlet="skeletonTpl" />
                            } @else {
                                <ng-container *ngTemplateOutlet="formTpl" />
                            }
                        </p-tabpanel>
                        <p-tabpanel value="fotos">
                            <app-photo-manager [propertyId]="id" />
                        </p-tabpanel>
                    </p-tabpanels>
                </p-tabs>
            } @else {
                <!-- Modo criação: formulário direto -->
                <ng-container *ngTemplateOutlet="formTpl" />
            }
        </div>

        <!-- Template: formulário completo -->
        <ng-template #formTpl>
            <form [formGroup]="form" (ngSubmit)="save()" class="flex flex-col gap-4">
                <!-- Seção 1: Informações Gerais -->
                <p-card header="Informações Gerais">
                    <div class="flex flex-col gap-4">
                        <div class="flex flex-col gap-2">
                            <label class="font-medium text-sm">Título <span class="text-red-500">*</span></label>
                            <input pInputText formControlName="title" placeholder="Ex: Casa 3 quartos no Jardim América" maxlength="200" class="w-full" />
                            @if (form.get('title')?.errors?.['required'] && form.get('title')?.touched) {
                                <small class="text-red-500">Título é obrigatório.</small>
                            }
                            @if (form.get('title')?.errors?.['maxlength'] && form.get('title')?.touched) {
                                <small class="text-red-500">Título deve ter no máximo 200 caracteres.</small>
                            }
                        </div>

                        <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-4">
                            <div class="flex flex-col gap-2">
                                <label class="font-medium text-sm">Categoria <span class="text-red-500">*</span></label>
                                <p-select formControlName="categoryId" [options]="categoryOptions()" optionLabel="label" optionValue="value" placeholder="Selecione..." [filter]="true" filterBy="label" styleClass="w-full" />
                                @if (form.get('categoryId')?.errors?.['required'] && form.get('categoryId')?.touched) {
                                    <small class="text-red-500">Categoria é obrigatória.</small>
                                }
                            </div>
                            <div class="flex flex-col gap-2">
                                <label class="font-medium text-sm">Tipo <span class="text-red-500">*</span></label>
                                <p-selectbutton formControlName="dealType" [options]="dealTypeOptions" optionLabel="label" optionValue="value" />
                            </div>
                            <div class="flex flex-col gap-2">
                                <label class="font-medium text-sm">Preço <span class="text-red-500">*</span></label>
                                <p-inputnumber formControlName="price" mode="currency" currency="BRL" locale="pt-BR" [min]="0.01" placeholder="0,00" styleClass="w-full" />
                                @if (form.get('price')?.errors?.['required'] && form.get('price')?.touched) {
                                    <small class="text-red-500">Preço é obrigatório.</small>
                                }
                            </div>
                            <div class="flex flex-col gap-2">
                                <label class="font-medium text-sm">Destaque</label>
                                <p-togglebutton formControlName="featured" onLabel="Em destaque" offLabel="Normal" onIcon="pi pi-star-fill" offIcon="pi pi-star" styleClass="w-full" />
                            </div>
                        </div>
                    </div>
                </p-card>

                <!-- Seção 2: Localização -->
                <p-card header="Localização">
                    <div class="flex flex-col gap-4">
                        <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-4">
                            <div class="flex flex-col gap-2">
                                <label class="font-medium text-sm">CEP</label>
                                <div class="flex items-center gap-2">
                                    <input pInputText formControlName="zipCode" placeholder="00000-000" maxlength="9" class="w-full" (input)="onZipInput($event)" (blur)="onZipBlur()" />
                                    @if (loadingZip()) {
                                        <p-progressspinner [style]="{ width: '1.25rem', height: '1.25rem' }" />
                                    }
                                </div>
                                @if (zipError()) {
                                    <p-message severity="warn" [text]="zipError()!" styleClass="w-full mt-1" />
                                }
                            </div>
                        </div>

                        <div class="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-4 gap-4">
                            <div class="flex flex-col gap-2 xl:col-span-2">
                                <label class="font-medium text-sm">Endereço</label>
                                <input pInputText formControlName="address" placeholder="Rua, número, complemento" class="w-full" />
                            </div>
                            <div class="flex flex-col gap-2">
                                <label class="font-medium text-sm">Bairro <span class="text-red-500">*</span></label>
                                <input pInputText formControlName="neighborhood" placeholder="Nome do bairro" class="w-full" />
                                @if (form.get('neighborhood')?.errors?.['required'] && form.get('neighborhood')?.touched) {
                                    <small class="text-red-500">Bairro é obrigatório.</small>
                                }
                            </div>
                            <div class="flex flex-col gap-2">
                                <label class="font-medium text-sm">Cidade <span class="text-red-500">*</span></label>
                                <input pInputText formControlName="city" placeholder="Nome da cidade" class="w-full" />
                                @if (form.get('city')?.errors?.['required'] && form.get('city')?.touched) {
                                    <small class="text-red-500">Cidade é obrigatória.</small>
                                }
                            </div>
                            <div class="flex flex-col gap-2">
                                <label class="font-medium text-sm">Estado <span class="text-red-500">*</span></label>
                                <p-select formControlName="state" [options]="ufOptions" optionLabel="label" optionValue="value" placeholder="UF" [filter]="true" filterBy="label" styleClass="w-full" />
                                @if (form.get('state')?.errors?.['required'] && form.get('state')?.touched) {
                                    <small class="text-red-500">Estado é obrigatório.</small>
                                }
                            </div>
                        </div>
                    </div>
                </p-card>

                <!-- Seção 3: Características -->
                <p-card header="Características">
                    <div class="grid grid-cols-2 md:grid-cols-3 xl:grid-cols-5 gap-4">
                        <div class="flex flex-col gap-2">
                            <label class="font-medium text-sm">Área (m²)</label>
                            <p-inputnumber formControlName="area" [min]="0" [maxFractionDigits]="2" placeholder="0" styleClass="w-full" />
                        </div>
                        <div class="flex flex-col gap-2">
                            <label class="font-medium text-sm">Dormitórios</label>
                            <p-inputnumber formControlName="bedrooms" [min]="0" placeholder="0" styleClass="w-full" />
                        </div>
                        <div class="flex flex-col gap-2">
                            <label class="font-medium text-sm">Suítes</label>
                            <p-inputnumber formControlName="suites" [min]="0" placeholder="0" styleClass="w-full" />
                        </div>
                        <div class="flex flex-col gap-2">
                            <label class="font-medium text-sm">Banheiros</label>
                            <p-inputnumber formControlName="bathrooms" [min]="0" placeholder="0" styleClass="w-full" />
                        </div>
                        <div class="flex flex-col gap-2">
                            <label class="font-medium text-sm">Vagas</label>
                            <p-inputnumber formControlName="parkingSpots" [min]="0" placeholder="0" styleClass="w-full" />
                        </div>
                    </div>
                </p-card>

                <!-- Seção 4: Informações Adicionais -->
                <p-card header="Informações Adicionais">
                    <div class="flex flex-col gap-2">
                        <label class="font-medium text-sm">Link Externo</label>
                        <input pInputText formControlName="externalUrl" placeholder="https://www.imobiliaria.com.br/imovel/123" class="w-full" />
                    </div>
                </p-card>

                <!-- Seção 5: Descrição -->
                <p-card header="Descrição">
                    <div class="flex flex-col gap-2">
                        <label class="font-medium text-sm">Descrição</label>
                        <textarea pTextarea formControlName="description" [autoResize]="true" rows="5" placeholder="Descreva o imóvel em detalhes..." class="w-full"></textarea>
                    </div>
                </p-card>

                <!-- Rodapé fixo -->
                <div class="sticky bottom-0 bg-surface-0 dark:bg-surface-900 border-t border-surface-200 dark:border-surface-700 -mx-2 px-6 py-4 flex justify-end gap-3 mt-2">
                    <p-button type="button" label="Cancelar" severity="secondary" [outlined]="true" (onClick)="cancel()" />
                    <p-button type="submit" [label]="isEditMode ? 'Salvar Alterações' : 'Salvar Imóvel'" icon="pi pi-check" [loading]="loadingForm()" [disabled]="form.invalid" />
                </div>
            </form>
        </ng-template>

        <!-- Template: skeleton de carregamento -->
        <ng-template #skeletonTpl>
            <div class="flex flex-col gap-4">
                <p-card>
                    <div class="flex flex-col gap-3">
                        <p-skeleton height="2rem" styleClass="w-full" />
                        <div class="grid grid-cols-4 gap-4">
                            <p-skeleton height="2.5rem" />
                            <p-skeleton height="2.5rem" />
                            <p-skeleton height="2.5rem" />
                            <p-skeleton height="2.5rem" />
                        </div>
                    </div>
                </p-card>
                <p-card>
                    <div class="grid grid-cols-4 gap-4">
                        <p-skeleton height="2.5rem" />
                        <p-skeleton height="2.5rem" styleClass="col-span-2" />
                        <p-skeleton height="2.5rem" />
                    </div>
                </p-card>
                <p-card>
                    <div class="grid grid-cols-5 gap-4">
                        <p-skeleton height="2.5rem" />
                        <p-skeleton height="2.5rem" />
                        <p-skeleton height="2.5rem" />
                        <p-skeleton height="2.5rem" />
                        <p-skeleton height="2.5rem" />
                    </div>
                </p-card>
            </div>
        </ng-template>
    `
})
export class PropertyFormComponent implements OnInit {
    @Input() id?: string;
    @Input() tab?: string;

    private readonly fb = inject(FormBuilder);
    private readonly propertyService = inject(PropertyService);
    private readonly categoryService = inject(CategoryService);
    private readonly zipCodeService = inject(ZipCodeService);
    private readonly router = inject(Router);
    private readonly destroyRef = inject(DestroyRef);

    readonly loading = signal(false);
    readonly loadingZip = signal(false);
    readonly loadingForm = signal(false);
    readonly zipError = signal<string | null>(null);
    readonly breadcrumbTitle = signal('Novo Imóvel');
    readonly activeTab = signal('dados');

    private zipCodePreloaded = false;

    private readonly categories = signal<{ id: string; name: string }[]>([]);
    readonly categoryOptions = computed(() => this.categories().map((c) => ({ label: c.name, value: c.id })));

    get isEditMode(): boolean {
        return !!this.id;
    }

    readonly breadcrumbItems = computed<MenuItem[]>(() => [
        { label: 'Imóveis', routerLink: '/properties' },
        { label: this.breadcrumbTitle() }
    ]);

    readonly breadcrumbHome: MenuItem = { icon: 'pi pi-home', routerLink: '/dashboard' };

    readonly dealTypeOptions = [
        { label: 'Venda', value: 'SALE' },
        { label: 'Aluguel', value: 'RENT' }
    ];

    readonly ufOptions = [
        { label: 'Acre (AC)', value: 'AC' },
        { label: 'Alagoas (AL)', value: 'AL' },
        { label: 'Amapá (AP)', value: 'AP' },
        { label: 'Amazonas (AM)', value: 'AM' },
        { label: 'Bahia (BA)', value: 'BA' },
        { label: 'Ceará (CE)', value: 'CE' },
        { label: 'Distrito Federal (DF)', value: 'DF' },
        { label: 'Espírito Santo (ES)', value: 'ES' },
        { label: 'Goiás (GO)', value: 'GO' },
        { label: 'Maranhão (MA)', value: 'MA' },
        { label: 'Mato Grosso (MT)', value: 'MT' },
        { label: 'Mato Grosso do Sul (MS)', value: 'MS' },
        { label: 'Minas Gerais (MG)', value: 'MG' },
        { label: 'Pará (PA)', value: 'PA' },
        { label: 'Paraíba (PB)', value: 'PB' },
        { label: 'Paraná (PR)', value: 'PR' },
        { label: 'Pernambuco (PE)', value: 'PE' },
        { label: 'Piauí (PI)', value: 'PI' },
        { label: 'Rio de Janeiro (RJ)', value: 'RJ' },
        { label: 'Rio Grande do Norte (RN)', value: 'RN' },
        { label: 'Rio Grande do Sul (RS)', value: 'RS' },
        { label: 'Rondônia (RO)', value: 'RO' },
        { label: 'Roraima (RR)', value: 'RR' },
        { label: 'Santa Catarina (SC)', value: 'SC' },
        { label: 'São Paulo (SP)', value: 'SP' },
        { label: 'Sergipe (SE)', value: 'SE' },
        { label: 'Tocantins (TO)', value: 'TO' }
    ];

    readonly form = this.fb.group({
        title: ['', [Validators.required, Validators.maxLength(200)]],
        categoryId: [null as string | null, Validators.required],
        dealType: ['SALE', Validators.required],
        price: [null as number | null, [Validators.required, Validators.min(0.01)]],
        featured: [false],
        zipCode: [''],
        address: [''],
        neighborhood: ['', Validators.required],
        city: ['', Validators.required],
        state: [null as string | null, Validators.required],
        area: [null as number | null],
        bedrooms: [null as number | null],
        suites: [null as number | null],
        bathrooms: [null as number | null],
        parkingSpots: [null as number | null],
        externalUrl: [''],
        description: ['']
    });

    ngOnInit(): void {
        this.categoryService.findAll().pipe(
            map((r) => r.data),
            takeUntilDestroyed(this.destroyRef)
        ).subscribe((cats) => this.categories.set(cats));

        if (this.tab) {
            this.activeTab.set(this.tab);
        }

        if (this.isEditMode) {
            this.loadProperty();
        }
    }

    private loadProperty(): void {
        this.loading.set(true);
        this.propertyService.findById(this.id!)
            .pipe(
                finalize(() => this.loading.set(false)),
                takeUntilDestroyed(this.destroyRef)
            )
            .subscribe({
                next: (r) => this.patchForm(r.data),
                error: () => {}
            });
    }

    private patchForm(p: PropertyDetail): void {
        this.breadcrumbTitle.set(p.title);
        this.zipCodePreloaded = true;
        this.form.patchValue({
            title: p.title,
            categoryId: p.categoryId ?? p.category?.id,
            dealType: p.dealType,
            price: p.price,
            featured: p.featured,
            zipCode: p.zipCode ?? '',
            address: p.address ?? '',
            neighborhood: p.neighborhood ?? '',
            city: p.city,
            state: p.state,
            area: p.area ?? null,
            bedrooms: p.bedrooms ?? null,
            suites: p.suites ?? null,
            bathrooms: p.bathrooms ?? null,
            parkingSpots: p.parkingSpots ?? null,
            externalUrl: p.externalUrl ?? '',
            description: p.description ?? ''
        });
    }

    onZipInput(event: Event): void {
        const input = event.target as HTMLInputElement;
        let value = input.value.replace(/\D/g, '').slice(0, 8);
        if (value.length > 5) value = `${value.slice(0, 5)}-${value.slice(5)}`;
        input.value = value;
        this.form.get('zipCode')?.setValue(value, { emitEvent: false });
        this.zipCodePreloaded = false;
    }

    onZipBlur(): void {
        if (this.zipCodePreloaded) return;
        const raw = (this.form.get('zipCode')?.value ?? '').replace(/\D/g, '');
        if (raw.length !== 8) return;
        this.loadingZip.set(true);
        this.zipError.set(null);
        this.zipCodeService
            .findByCode(raw, new HttpContext().set(SKIP_ERROR_TOAST, true))
            .pipe(
                finalize(() => this.loadingZip.set(false)),
                takeUntilDestroyed(this.destroyRef)
            )
            .subscribe({
                next: (r) => {
                    this.form.patchValue({ address: r.data.street, neighborhood: r.data.district, city: r.data.city, state: r.data.state });
                },
                error: () => {
                    this.zipError.set('CEP não encontrado. Preencha o endereço manualmente.');
                }
            });
    }

    save(): void {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            return;
        }
        this.loadingForm.set(true);
        const payload = this.form.getRawValue() as unknown as PropertyForm;

        if (this.isEditMode) {
            this.propertyService.update(this.id!, payload)
                .pipe(
                    finalize(() => this.loadingForm.set(false)),
                    takeUntilDestroyed(this.destroyRef)
                )
                .subscribe({ error: () => {} });
        } else {
            this.propertyService.create(payload)
                .pipe(
                    finalize(() => this.loadingForm.set(false)),
                    takeUntilDestroyed(this.destroyRef)
                )
                .subscribe({
                    next: (r) => this.router.navigate(['/properties', r.data.id, 'edit'], { queryParams: { tab: 'fotos' } }),
                    error: () => {}
                });
        }
    }

    cancel(): void {
        this.router.navigate(['/properties']);
    }
}
