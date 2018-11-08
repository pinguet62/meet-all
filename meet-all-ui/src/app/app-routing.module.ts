import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

const routes: Routes = [];

@NgModule({
    imports: [RouterModule.forRoot([{path: '', loadChildren: './tabs/tabs.module#TabsPageModule'}])],
    exports: [RouterModule],
})
export class AppRoutingModule {
}
