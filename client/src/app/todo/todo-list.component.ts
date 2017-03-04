import { Component, OnInit } from '@angular/core';
import { TodoListService } from "./todo-list.service";
import { Todo } from "./todo";
import { FilterBy } from "./filter.pipe";
import {filter} from "rxjs/operator/filter";

@Component({
    selector: 'todo-list-component',
    templateUrl: 'todo-list.component.html',
    providers: [ FilterBy ]
})

export class TodoListComponent implements OnInit{
    public todos: Todo[];

    constructor(private todoListService: TodoListService) {
        // this.users = this.userListService.getUsers();
    }

    getFilterVals(): string {
        var filterVals = "?";

        if ((<HTMLInputElement>document.getElementById("owner")).checked) {
            filterVals +=
                "&owner=" + (<HTMLInputElement>document.getElementById("ownerVal")).value;
        }

        if ((<HTMLInputElement>document.getElementById("body")).checked) {
            filterVals +=
                "&body=" + (<HTMLInputElement>document.getElementById("bodyVal")).value;
        }

        if ((<HTMLInputElement>document.getElementById("category")).checked) {
            filterVals +=
                "&category=" + (<HTMLInputElement>document.getElementById("categoryVal")).value;
        }

        if ((<HTMLInputElement>document.getElementById("status")).checked) {
            filterVals +=
                "&status=" + (<HTMLInputElement>document.getElementById("statusVal")).value;
        }

        return filterVals;
    }

    clickToFilter(): void {
        var filterVals = this.getFilterVals();

        this.todoListService.filterTodos(filterVals).subscribe(
            todos => this.todos = todos,
            err => {
                console.log(err);
            }
        );
    }

    ngOnInit(): void {
        this.todoListService.getTodos().subscribe(
            todos => this.todos = todos,
            err => {
                console.log(err);
            }
        );
    }
}