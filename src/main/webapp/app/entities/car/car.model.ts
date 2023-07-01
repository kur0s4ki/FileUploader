export interface ICar {
  id: number;
  model?: string | null;
}

export type NewCar = Omit<ICar, 'id'> & { id: null };
