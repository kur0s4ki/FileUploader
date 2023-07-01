import { IContent, NewContent } from './content.model';

export const sampleWithRequiredData: IContent = {
  id: 71035,
  data: '../fake-data/blob/hipster.png',
  dataContentType: 'unknown',
};

export const sampleWithPartialData: IContent = {
  id: 98289,
  data: '../fake-data/blob/hipster.png',
  dataContentType: 'unknown',
};

export const sampleWithFullData: IContent = {
  id: 53362,
  data: '../fake-data/blob/hipster.png',
  dataContentType: 'unknown',
};

export const sampleWithNewData: NewContent = {
  data: '../fake-data/blob/hipster.png',
  dataContentType: 'unknown',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
