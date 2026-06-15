import {
  ChangeDetectionStrategy,
  Component,
  computed,
  input,
  OnInit,
  signal,
} from '@angular/core';

import { PropertyPhoto } from '../../../../core/models/property.model';

@Component({
  selector: 'app-photo-gallery',
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './photo-gallery.component.html',
})
export class PhotoGalleryComponent implements OnInit {
  readonly photos = input<PropertyPhoto[]>([]);
  readonly dealType = input<'SALE' | 'RENT' | null>(null);
  readonly category = input<{ name: string } | null>(null);

  readonly activePhoto = signal<PropertyPhoto | null>(null);

  readonly dealTypeLabel = computed(() =>
    this.dealType() === 'SALE' ? 'Venda' : this.dealType() === 'RENT' ? 'Aluguel' : null,
  );

  ngOnInit(): void {
    const photos = this.photos();
    if (photos.length > 0) {
      this.activePhoto.set(photos.find(p => p.cover) ?? photos[0]);
    }
  }

  protected thumbnailClass(photo: PropertyPhoto): string {
    const isActive = this.activePhoto()?.id === photo.id;
    return isActive
      ? 'border border-gold overflow-hidden rounded-sm w-20 aspect-[4/3] lg:w-full lg:aspect-[4/3] shrink-0 transition-opacity duration-200'
      : 'border border-border/60 opacity-70 hover:opacity-100 overflow-hidden rounded-sm w-20 aspect-[4/3] lg:w-full lg:aspect-[4/3] shrink-0 transition-opacity duration-200 cursor-pointer';
  }
}
