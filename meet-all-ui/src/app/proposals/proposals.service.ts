import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';

export interface Profile {
    id: string;
    name: string;
    age: number;
    avatars: string[];
}

export interface Proposal {
    id: string;
    profile: Profile;
}

@Injectable()
export class ProposalsService {

    constructor(private http: HttpClient) {
    }

    getProposals(): Observable<Proposal[]> {
        return this.http.get<Proposal[]>(environment.apiUrl + '/proposals');
    }

    unlikeProposal(proposalId: string): Observable<void> {
        return this.http.post<void>(environment.apiUrl + `/proposals/${encodeURIComponent(proposalId)}/unlike`, null);
    }

    likeProposal(proposalId: string): Observable<boolean> {
        return this.http.post<boolean>(environment.apiUrl + `/proposals/${encodeURIComponent(proposalId)}/like`, null);
    }

}
