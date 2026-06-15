export interface Property {
  id: string;
  title: string;
  slug: string;
  description?: string;
  price: number;
  area?: number;
  bedrooms?: number;
  suites?: number;
  bathrooms?: number;
  parkingSpots?: number;
  address?: string;
  city: string;
  neighborhood?: string;
  state?: string;
  zipCode?: string;
  dealType: 'SALE' | 'RENT';
  featured: boolean;
  status: 'ACTIVE' | 'INACTIVE';
  externalUrl?: string;
  category?: { id: string; name: string; slug: string };
  coverPhoto?: string;
  photos?: PropertyPhoto[];
  features?: string[];
}

export interface PropertyPhoto {
  id: string;
  url: string;
  order: number;
  cover: boolean;
}

export interface PropertyFilterSelection {
  dealType: 'SALE' | 'RENT' | null;
  categorySlug: string | null;
  city: string | null;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
}
