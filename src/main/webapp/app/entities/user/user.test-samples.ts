import { IUser } from './user.model';

export const sampleWithRequiredData: IUser = {
  id: 6303,
  login: 'QtT',
};

export const sampleWithPartialData: IUser = {
  id: 20081,
  login: 'KS-Ta',
};

export const sampleWithFullData: IUser = {
  id: 9366,
  login: 'D2WmC',
};
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
