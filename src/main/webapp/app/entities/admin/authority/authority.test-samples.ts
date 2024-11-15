import { IAuthority, NewAuthority } from './authority.model';

export const sampleWithRequiredData: IAuthority = {
  name: 'a0840ef6-4b2d-4c75-bb76-f8e68982c052',
};

export const sampleWithPartialData: IAuthority = {
  name: '9a3bd5c1-da3b-45a2-8c78-40c581880941',
};

export const sampleWithFullData: IAuthority = {
  name: '238594f1-de11-472e-a409-d21437757b70',
};

export const sampleWithNewData: NewAuthority = {
  name: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
