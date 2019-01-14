import {CommonModule} from '@angular/common';
import {HttpClientModule} from '@angular/common/http';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {IonicModule} from '@ionic/angular';
import {SecurityModule} from '../../security';
import {ConversationsService} from '../conversations.service';
import {ConversationListComponent} from './conversation-list.component';
import {BadgeIconModule} from '../../shared/badge-icon';
import {ProxifiedSrcModule} from '../../shared/proxifiedSrc';

@NgModule({
    imports: [
        // libs
        IonicModule,
        CommonModule,
        HttpClientModule,
        FormsModule,
        // app
        BadgeIconModule,
        ProxifiedSrcModule,
        RouterModule.forChild([{path: '', component: ConversationListComponent}]),
        SecurityModule,
    ],
    declarations: [
        ConversationListComponent,
    ],
    providers: [
        ConversationsService,
    ],
})
export class ConversationListModule {
}
