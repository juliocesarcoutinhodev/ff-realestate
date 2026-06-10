export interface Testimonial {
    id: string;
    clientName: string;
    text: string;
    rating: number;
    status: 'PENDING' | 'APPROVED' | 'REJECTED';
    property?: { id: string; title: string; slug: string };
    createdAt: string;
}
