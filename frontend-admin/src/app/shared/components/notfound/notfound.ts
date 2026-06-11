import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { AppFloatingConfigurator } from '@/app/layout/component/app.floatingconfigurator';

@Component({
    selector: 'app-notfound',
    standalone: true,
    imports: [RouterModule, AppFloatingConfigurator, ButtonModule],
    template: `
        <app-floating-configurator />
        <div class="bg-surface-50 dark:bg-surface-950 flex items-center justify-center min-h-screen min-w-screen overflow-hidden">
            <div class="flex flex-col items-center justify-center">
                <div style="border-radius: 56px; padding: 0.3rem; background: linear-gradient(180deg, color-mix(in srgb, var(--primary-color), transparent 60%) 10%, var(--surface-ground) 30%)">
                    <div class="w-full bg-surface-0 dark:bg-surface-900 py-20 px-8 sm:px-20 flex flex-col items-center" style="border-radius: 53px">
                        <div class="flex justify-center items-center border-2 border-primary rounded-full mb-6" style="width: 3.2rem; height: 3.2rem">
                            <i class="text-primary pi pi-fw pi-search text-2xl!"></i>
                        </div>
                        <span class="text-primary font-bold text-3xl">404</span>
                        <h1 class="text-surface-900 dark:text-surface-0 font-bold text-3xl lg:text-5xl mb-4 mt-2">Página não encontrada</h1>
                        <span class="text-muted-color mb-8 text-center">A página que você tentou acessar não existe ou foi removida.</span>
                        <p-button label="Voltar ao Dashboard" routerLink="/dashboard" />
                    </div>
                </div>
            </div>
        </div>
    `
})
export class Notfound {}
