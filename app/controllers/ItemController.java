package controllers;

import javax.inject.Inject;

import models.Item;
import models.Stock;
import play.i18n.MessagesApi;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;

import java.util.*;


public class ItemController extends Controller {

    FormFactory formFactory;
    MessagesApi messagesApi;
    @Inject
    public ItemController(FormFactory formFactory, MessagesApi messagesApi) {
        this.formFactory=formFactory;
        this.messagesApi=messagesApi;
    }

    public Result index(){
        List<Item> items = Item.find.all();
        return ok(views.html.items.index.render(items));
    }

    public Result create(Http.Request request){
        Form<Item> itemForm = formFactory.form(Item.class);
        return ok(views.html.items.create.render(itemForm,request, messagesApi.preferred(request)));
    }

    public Result save(Http.Request request){
        Form<Item> itemForm = formFactory.form(Item.class).bindFromRequest(request);

        if(itemForm.hasErrors()){
            return badRequest(views.html.items.create.render(itemForm,request, messagesApi.preferred(request)));
        }

        Item item = itemForm.get();

        try {
            item.save();
        }
        catch (Exception e) {
            return internalServerError(e.toString());
        }

        return redirect(routes.ItemController.index());
    }

    public Result edit(Integer id,Http.Request request){

        Item item = Item.find.byId(id);
        if(item==null){
            return notFound("Item with id "+id+" not found");
        }
        Form<Item> itemForm = formFactory.form(Item.class).fill(item);

        return ok(views.html.items.edit.render(itemForm,request, messagesApi.preferred(request)));
    }

    public Result update(Http.Request request){
        Form<Item> itemForm = formFactory.form(Item.class).bindFromRequest(request);

        if(itemForm.hasErrors()){
            return badRequest(views.html.items.create.render(itemForm,request, messagesApi.preferred(request)));
        }

        Item item = itemForm.get();
        Item oldItem = Item.find.byId(item.itemId);

        if(oldItem==null){
            return notFound("Item not found");
        }

        oldItem.setItemId(item.itemId);
        oldItem.setName(item.name);

        oldItem.update();
        return ok();
    }

    public Result delete(Integer id){
        Item item = Item.find.byId(id);
        if(item==null){
            return notFound("Item not found");
        }
        item.delete();
        return ok();
    }

    public Result show(Integer id){

        Item item = Item.find.byId(id);
        if(item==null){
            return notFound("Item not found");
        }

        return ok(views.html.items.show.render(item));
    }
}
