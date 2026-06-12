export interface Property {
    id: string;
    title: string;
    slug: string;
    price: number;
    area: number;
    bedrooms: number;
    suites: number;
    bathrooms: number;
    parkingSpots: number;
    city: string;
    neighborhood: string;
    dealType: 'SALE' | 'RENT';
    featured: boolean;
    status: 'ACTIVE' | 'INACTIVE';
    externalUrl?: string;
    categoryId: string;
    coverPhoto?: string;
}

export interface PropertyForm {
    title: string;
    description?: string;
    price: number;
    area?: number;
    bedrooms?: number;
    suites?: number;
    bathrooms?: number;
    parkingSpots?: number;
    address?: string;
    neighborhood?: string;
    city: string;
    state: string;
    zipCode?: string;
    dealType: 'SALE' | 'RENT';
    featured: boolean;
    externalUrl?: string;
    categoryId: string;
}

export interface PropertyFilters {
    status?: 'ACTIVE' | 'INACTIVE';
    dealType?: 'SALE' | 'RENT';
    categoryId?: string;
    page?: number;
    size?: number;
}

export interface ZipCodeResponse {
    code: string;
    street: string;
    district: string;
    city: string;
    state: string;
}

export interface PropertyDetail {
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
    neighborhood?: string;
    city: string;
    state: string;
    zipCode?: string;
    dealType: 'SALE' | 'RENT';
    featured: boolean;
    status: 'ACTIVE' | 'INACTIVE';
    externalUrl?: string;
    categoryId: string;
    category: { id: string; name: string; slug: string };
    photos: { id: string; url: string; order: number }[];
}
