import { ComponentFixture, TestBed, async } from "@angular/core/testing";
import { Todo } from "./todo";
import { TodoComponent } from "./todo.component";
import { TodoListService } from "./todo-list.service";
import { Observable } from "rxjs";
import { PipeModule } from "../../pipe.module";

describe("Todo component", () => {

    let todoComponent: TodoComponent;
    let fixture: ComponentFixture<TodoComponent>;

    let todoListServiceStub: {
        getTodoById: (todoId: string) => Observable<Todo>
    };

    beforeEach(() => {
        // stub UserService for test purposes
        todoListServiceStub = {
            getTodoById: (todoId: string) => Observable.of([
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
            ].find(todo => todo._id === todoId))
        };

        TestBed.configureTestingModule({
            imports: [PipeModule],
            declarations: [ TodoComponent ],
            providers:    [ { provide: TodoListService, useValue: todoListServiceStub } ]
        })
    });

    beforeEach(async(() => {
        TestBed.compileComponents().then(() => {
            fixture = TestBed.createComponent(TodoComponent);
            todoComponent = fixture.componentInstance;
        });
    }));

    it("can retrieve Barry by ID", () => {
        todoComponent.setId("barry_id");
        expect(todoComponent.todo).toBeDefined();
        expect(todoComponent.todo.owner).toBe("Barry");
        expect(todoComponent.todo.category).toBe("groceries");
    });

    it("returns undefined for Santa", () => {
        todoComponent.setId("Santa");
        expect(todoComponent.todo).not.toBeDefined();
    });

});
