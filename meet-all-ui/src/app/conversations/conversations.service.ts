import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';
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

    public getConversations(): Observable<Conversation[]> {
        return this.http.get<Conversation[]>(environment.apiUrl + '/conversations');
    }

    /** @return Ordered by {@link Message#date} descending. */
    public getMessagesByConversation(conversationId: string): Observable<Message[]> {
        return of([
            {id: 'mes1', date: new Date(new Date().getTime() + 4/*min*/ * 60/*sec*/ * 1/*sec*/ * 1000), sent: true, text: 'text1'},
            {id: 'mes2', date: new Date(new Date().getTime() + 3/*min*/ * 60/*sec*/ * 1/*sec*/ * 1000), sent: false, text: '😅'},
            {
                id: 'mes3',
                date: new Date(new Date().getTime() + 2/*min*/ * 60/*sec*/ * 1/*sec*/ * 1000),
                sent: false,
                text: 'Even if girls don\'t speak, this is a very long message, for visual testing, with line breaks because of screen width!'
            },
            {
                id: 'mes4',
                date: new Date(new Date().getTime() + 1/*min*/ * 60/*sec*/ * 1/*sec*/ * 1000),
                sent: true,
                text: 'This is a very long message with line breaks because of screen width \nor voluntary!'
            },
        ]);
    }

}
