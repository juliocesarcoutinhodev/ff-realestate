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
