import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';

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

export enum Provider {
    TINDER = 'TINDER',
}

export interface RegisteredCredential {
    id: number;
    provider: Provider;
    label: string;
}

@Injectable()
export class Services {

    public list(): Observable<Conversation[]> {
        return of([
            {
                id: 'conv1',
                profile: {
                    id: 'prof1',
                    name: 'name1',
                    age: 1,
                    avatars: ['https://raw.githubusercontent.com/ionic-team/ionic-preview-app/master/src/assets/img/avatar-ben.png']
                },
                lastMessage: {id: 'mes1', date: new Date(), sent: true, text: 'text1'}
            },
            {
                id: 'conv2',
                profile: {
                    id: 'prof2',
                    name: 'name2',
                    age: 2,
                    avatars: ['https://raw.githubusercontent.com/ionic-team/ionic-preview-app/master/src/assets/img/avatar-finn.png']
                },
                lastMessage: {id: 'mes2', date: new Date(), sent: false, text: 'text2'}
            }
        ]);
    }

    public getRegisteredCredential(): Observable<RegisteredCredential[]> {
        return of([
            {id: 1, provider: Provider.TINDER, label: 'First'},
            {id: 2, provider: Provider.TINDER, label: 'Second'},
            {id: 3, provider: Provider.TINDER, label: 'Third'}
        ]);
    }

}
