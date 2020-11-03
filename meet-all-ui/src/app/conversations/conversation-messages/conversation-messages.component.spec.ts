import {NO_ERRORS_SCHEMA} from '@angular/core';
import {async, fakeAsync, TestBed} from '@angular/core/testing';
import {By} from '@angular/platform-browser';
import {ActivatedRoute, convertToParamMap, RouterModule} from '@angular/router';
import {IonicModule, LoadingController} from '@ionic/angular';
import {of} from 'rxjs';
import {allMethodNames, LoadingControllerFake} from '../../test-utils';
import {ConversationMessagesComponent} from './conversation-messages.component';
import {ConversationsService, Message, Profile} from '../conversations.service';

xdescribe('conversation-messages.page', () => {
    let activatedRoute: ActivatedRoute;
    let conversationsService: jasmine.SpyObj<ConversationsService>;
    beforeEach(() => {
        activatedRoute = {
            snapshot: {
                paramMap: convertToParamMap({
                    conversationId: 'conversationId',
                    profileId: 'profileId',
                })
            }
        } as ActivatedRoute;
        conversationsService = jasmine.createSpyObj<ConversationsService>(ConversationsService.name, allMethodNames(ConversationsService));
    });

    beforeEach(async(() => {
        TestBed.configureTestingModule({
            declarations: [ConversationMessagesComponent],
            providers: [
                {provide: ActivatedRoute, useValue: activatedRoute},
                {provide: LoadingController, useClass: LoadingControllerFake},
                {provide: ConversationsService, useValue: conversationsService},
            ],
            imports: [
                IonicModule.forRoot(),
                RouterModule.forRoot([]),
            ],
            schemas: [NO_ERRORS_SCHEMA],
        });
    }));

    it('<input> & <button> lifecycle', fakeAsync(() => {
        conversationsService.getProfile.and.returnValue(of<Profile>({id: 'id', name: 'name', age: 42, description: 'description', avatars: []}));
        conversationsService.getMessagesByConversation.and.returnValue(of<Message[]>([]));
        const sentMessage: Message = {id: 'id', date: new Date(), sent: true, text: 'text'};
        conversationsService.sendMessage.and.returnValue(of(sentMessage));

        const fixture = TestBed.createComponent(ConversationMessagesComponent);
        fixture.autoDetectChanges(true);
        fixture.detectChanges();

        const input = fixture.debugElement.query(By.css('ion-footer')).query(By.css('ion-input')).componentInstance;
        const buttonDebugElement = fixture.debugElement.query(By.css('ion-footer')).query(By.css('ion-button'));
        const ionButton = buttonDebugElement.componentInstance;
        console.log('> input', input);
        console.log('> buttonDebugElement', buttonDebugElement);
        console.log('> ionButton', ionButton);

        // #1 initial: empty & disabled

        expect(input.value).toBeUndefined(); // .toEqual('');
        expect(ionButton.disabled).toBeTruthy();

        // #2 fill: enabled

        fixture.componentInstance.text = 'text'; // TODO: input.value = 'text';
        fixture.detectChanges();

        expect(ionButton.disabled).toBeFalsy();

        // #3 click on send: disabled & loading

        expect(buttonDebugElement.query(By.css('ion-icon[name="send"]'))).not.toBeNull();
        expect(buttonDebugElement.query(By.css('ion-spinner'))).toBeNull();

        buttonDebugElement.triggerEventHandler('click', null);
        fixture.detectChanges();

        expect(buttonDebugElement.query(By.css('ion-spinner'))).not.toBeNull();
        expect(buttonDebugElement.query(By.css('ion-icon[name="send"]'))).toBeNull();
        expect(conversationsService.sendMessage).toHaveBeenCalledWith('conversationId', 'text');
        console.log('send', fixture.debugElement.query(By.css('ion-footer')).query(By.css('ion-button')).query(By.css('ion-icon[name="send"]')) != null);
        console.log('spinner', fixture.debugElement.query(By.css('ion-footer')).query(By.css('ion-button')).query(By.css('ion-spinner')) != null);

        // // #4 completed: cleared & disabled
        //
        // // TODO complete
        //
        // expect(input.value).toEqual('');
        // expect(button.query(By.css('ion-icon[name="send"]'))).toBeDefined();
        // expect(button.query(By.css('ion-spinner'))).not.toBeDefined();
        // expect(button.attributes['ng-reflect-disabled']).toBeTruthy();
        // expect(fixture.componentInstance.messages).toEqual([sentMessage]);
    }));
});
