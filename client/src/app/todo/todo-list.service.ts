import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Todo } from './todo';
import { Observable } from "rxjs";

@Injectable()
export class TodoListService {
    private todoUrl: string = API_URL + "todos";
    constructor(private http:Http) { }

    getAllTodos(): Observable<Todo[]> {
        return this.http.request(this.todoUrl).map(res => res.json());
    }

    filterTodosByOwner(owner: string): Observable<Todo[]> {
        return this.http.request(this.todoUrl + "?owner=" + owner).map(res => res.json());
    }

    filterTodosByStatus(status: string): Observable<Todo[]> {
        return this.http.request(this.todoUrl + "?status=" + status).map(res => res.json());
    }

    filterTodosByBody(body: string): Observable<Todo[]> {
        return this.http.request(this.todoUrl + "?body=" + body).map(res => res.json());
    }

    getTodoById(id: string): Observable<Todo> {
        return this.http.request(this.todoUrl + "/" + id).map(res => res.json());
    }
}