import { Component, OnInit } from '@angular/core';
import { TodoListService } from "./todo-list.service";
import { Todo } from "./todo";
import { FilterBy } from "./filter.pipe";

@Component({
    selector: 'todo-list-component',
    templateUrl: 'todo-list.component.html',
    providers: [ FilterBy ]
})

export class TodoListComponent {
    public todos: Todo[];
    public searchOwner: string;
    public searchStatus: string;
    public searchBody: string;

    constructor(private todoListService: TodoListService) {
        // this.users = this.userListService.getUsers();
    }

    enterFilterVal(): void {
        this.todoListService.filterTodosByField(this.searchOwner, this.searchStatus, this.searchBody).subscribe(
            todos => this.todos = todos,
            err => {
                console.log(err);
            }
        );
    }
}