import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IEnterprise } from '../enterprise.model';

@Component({
  selector: 'jhi-enterprise-detail',
  templateUrl: './enterprise-detail.component.html',
})
export class EnterpriseDetailComponent implements OnInit {
  enterprise: IEnterprise | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ enterprise }) => {
      this.enterprise = enterprise;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
