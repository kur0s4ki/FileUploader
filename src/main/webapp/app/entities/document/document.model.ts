import { IContent } from 'app/entities/content/content.model';
import { ICar } from 'app/entities/car/car.model';

export interface IDocument {
  id: number;
  title?: string | null;
  size?: number | null;
  mimeType?: string | null;
  content?: Pick<IContent, 'id'> | null;
  car?: Pick<ICar, 'id'> | null;
}

export type NewDocument = Omit<IDocument, 'id'> & { id: null };
