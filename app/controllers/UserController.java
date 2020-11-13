package controllers;

import javax.inject.Inject;

import play.i18n.MessagesApi;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;
import models.User;
import java.util.*;

public class UserController extends Controller {

    FormFactory formFactory;
    MessagesApi messagesApi;
    @Inject
    public UserController(FormFactory formFactory, MessagesApi messagesApi) {
        this.formFactory=formFactory;
        this.messagesApi=messagesApi;
    }

    public Result index(){
        List<User> users = User.find.all();
        return ok(views.html.users.index.render(users));
    }

    public Result create(Http.Request request){
        Form<User> userForm = formFactory.form(User.class);
        return ok(views.html.users.create.render(userForm,request, messagesApi.preferred(request)));
    }

    public Result save(Http.Request request){
        Form<User> userForm = formFactory.form(User.class).bindFromRequest(request);

        if(userForm.hasErrors()){
            return badRequest(views.html.users.create.render(userForm,request, messagesApi.preferred(request)));
        }

        User user = userForm.get();

        try {
            user.save();
        }
        catch (Exception e) {
            return internalServerError(e.toString());
        }

        return redirect(routes.UserController.index());
    }

    public Result edit(Integer id,Http.Request request){

        User user = User.find.byId(id);
        if(user==null){
            return notFound("User with id "+id+" not found");
        }
        Form<User> userForm = formFactory.form(User.class).fill(user);

        return ok(views.html.users.edit.render(userForm,request, messagesApi.preferred(request)));
    }

    public Result update(Http.Request request){
        Form<User> userForm = formFactory.form(User.class).bindFromRequest(request);

        if(userForm.hasErrors()){
            return badRequest(views.html.users.create.render(userForm,request, messagesApi.preferred(request)));
        }

        User user = userForm.get();
        User oldUser = User.find.byId(user.userId);

        if(oldUser==null){
            return notFound("User not found");
        }

        oldUser.userId=user.userId;
        oldUser.name=user.name;
        oldUser.email=user.email;

        oldUser.update();
        return ok();
    }

    public Result delete(Integer id){
        User user = User.find.byId(id);
        if(user==null){
            return notFound("User not found");
        }
        user.delete();
        return ok();
    }

    public Result show(Integer id){

        User user = User.find.byId(id);
        if(user==null){
            return notFound("User not found");
        }

        return ok(views.html.users.show.render(user));
    }
}
