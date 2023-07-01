import { ICar, NewCar } from './car.model';

export const sampleWithRequiredData: ICar = {
  id: 97049,
  model: 'Parkways',
};

export const sampleWithPartialData: ICar = {
  id: 69889,
  model: 'Gasoline',
};

export const sampleWithFullData: ICar = {
  id: 74445,
  model: 'Ball',
};

export const sampleWithNewData: NewCar = {
  model: 'Cisgender South',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
