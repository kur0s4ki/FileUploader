import { IDocument, NewDocument } from './document.model';

export const sampleWithRequiredData: IDocument = {
  id: 66555,
  title: 'relationships Music',
  size: 13958,
};

export const sampleWithPartialData: IDocument = {
  id: 65971,
  title: 'given hard',
  size: 23847,
};

export const sampleWithFullData: IDocument = {
  id: 94051,
  title: 'curio',
  size: 14028,
  mimeType: 'structure Meadows sensor',
};

export const sampleWithNewData: NewDocument = {
  title: 'Tuna female',
  size: 45182,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
