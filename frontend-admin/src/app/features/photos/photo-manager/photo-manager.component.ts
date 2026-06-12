import { Component, Input } from '@angular/core';
import { Message } from 'primeng/message';

@Component({
    selector: 'app-photo-manager',
    standalone: true,
    imports: [Message],
    template: `
        <p-message
            severity="info"
            text="Gerenciamento de fotos será implementado no EPIC-05."
            styleClass="w-full"
        />
    `
})
export class PhotoManagerComponent {
    @Input() propertyId?: string;
}
