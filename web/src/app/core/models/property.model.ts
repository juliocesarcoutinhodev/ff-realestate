export interface Property {
  id: string;
  title: string;
  slug: string;
  price: number;
  area?: number;
  bedrooms?: number;
  suites?: number;
  bathrooms?: number;
  parkingSpots?: number;
  city: string;
  neighborhood?: string;
  dealType: 'SALE' | 'RENT';
  featured: boolean;
  status: 'ACTIVE' | 'INACTIVE';
  externalUrl?: string;
  category: { id: string; name: string; slug: string };
  coverPhoto?: string;
  photos?: PropertyPhoto[];
}

export interface PropertyPhoto {
  id: string;
  url: string;
  orderIndex: number;
  cover: boolean;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
}
