import { ComponentFixture, TestBed, async } from "@angular/core/testing";
import { Todo } from "./todo";
import { TodoListComponent } from "./todo-list.component";
import { TodoListService } from "./todo-list.service";
import { Observable } from "rxjs";
import { PipeModule } from "../../pipe.module";

describe("Todo list", () => {

    let todoList: TodoListComponent;
    let fixture: ComponentFixture<TodoListComponent>;

    let todoListServiceStub: {
        getTodos: () => Observable<Todo[]>
    };

    beforeEach(() => {
        // stub UserService for test purposes
        todoListServiceStub = {
            getTodos: () => Observable.of([
                {
                    _id: "barry_id",
                    owner: "Barry",
                    status: true,
                    body: "sunny day",
                    category: "groceries"
                },
                {
                    _id: "blanche_id",
                    owner: "Blanche",
                    status: true,
                    body: "rainy day",
                    category: "video games"
                },
                {
                    _id: "fry_id",
                    owner: "Fry",
                    status: false,
                    body: "beautiful day",
                    category: "groceries"
                }
            ])
        };

        TestBed.configureTestingModule({
            imports: [PipeModule],
            declarations: [ TodoListComponent ],
            // providers:    [ UserListService ]  // NO! Don't provide the real service!
            // Provide a test-double instead
            providers:    [ { provide: TodoListService, useValue: todoListServiceStub } ]
        })
    });

    beforeEach(async(() => {
        TestBed.compileComponents().then(() => {
            fixture = TestBed.createComponent(TodoListComponent);
            todoList = fixture.componentInstance;
            fixture.detectChanges();
        });
    }));

    it("contains all the todos", () => {
        expect(todoList.todos.length).toBe(3);
    });

    it("contains an owner named 'Barry'", () => {
        expect(todoList.todos.some((todo: Todo) => todo.owner === "Barry" )).toBe(true);
    });

    it("contains an owner named 'Blanche'", () => {
        expect(todoList.todos.some((todo: Todo) => todo.owner === "Blanche" )).toBe(true);
    });

    it("doesn't contain an owner named 'Santa'", () => {
        expect(todoList.todos.some((todo: Todo) => todo.owner === "Santa" )).toBe(false);
    });

    it("has two todos whose status 'true'", () => {
        expect(todoList.todos.filter((todo: Todo) => todo.status === true).length).toBe(2);
    });

});
