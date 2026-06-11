export interface Category {
    id: string;
    name: string;
    slug: string;
    description?: string;
}

export interface CategoryForm {
    name: string;
    description?: string;
}
