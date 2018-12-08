import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {HttpClient} from '@angular/common/http';

export interface Profile {
    id: string;
    name: string;
    age: number;
    avatars: string[];
}

export interface Message {
    id: string;
    date: Date;
    sent: boolean;
    text: string;
}

export interface Conversation {
    id: string;
    profile: Profile;
    lastMessage: Message;
}

@Injectable()
export class ConversationsService {

    constructor(private http: HttpClient) {
    }

    /** @return Ordered by {@link Conversation#date} descending. */
    public getConversations(): Observable<Conversation[]> {
        return this.http.get<Conversation[]>(environment.apiUrl + '/conversations');
    }

    /** @return Ordered by {@link Message#date} descending. */
    public getMessagesByConversation(conversationId: string): Observable<Message[]> {
        return this.http.get<Message[]>(environment.apiUrl + `/conversations/${encodeURIComponent(conversationId)}/messages`);
    }

    public getProfile(profileId: string): Observable<Profile> {
        return this.http.get<Profile>(environment.apiUrl + `/profile/${encodeURIComponent(profileId)}`);
    }

}
