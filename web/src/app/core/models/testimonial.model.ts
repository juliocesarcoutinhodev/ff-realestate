export interface Testimonial {
  id: string;
  clientName: string;
  clientRole?: string;
  text: string;
  rating: number;
  property?: { id: string; title: string; slug: string };
  createdAt: string;
}
