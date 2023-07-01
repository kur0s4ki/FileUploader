export interface IContent {
  id: number;
  data?: string | null;
  dataContentType?: string | null;
}

export type NewContent = Omit<IContent, 'id'> & { id: null };
