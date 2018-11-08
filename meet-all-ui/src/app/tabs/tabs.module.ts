import {IonicModule} from '@ionic/angular';
import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {TabsPageRoutingModule} from './tabs-routing.module';
import {TabsPage} from './tabs.page';

@NgModule({
    imports: [
        // libs
        IonicModule,
        CommonModule,
        FormsModule,
        // app
        TabsPageRoutingModule,
    ],
    declarations: [TabsPage],
})
export class TabsPageModule {
}
