package controllers;

import javax.inject.Inject;

import models.*;
import play.i18n.MessagesApi;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.*;
import java.util.*;
import play.api.libs.mailer.MailerClient;
import play.libs.mailer.Email;

public class OrderController extends Controller {
    private final MailerClient mailer;

    FormFactory formFactory;
    MessagesApi messagesApi;
    @Inject
    public OrderController(FormFactory formFactory, MessagesApi messagesApi, MailerClient mailer) {
    this.formFactory=formFactory;
    this.messagesApi=messagesApi;
    this.mailer = mailer;
    }

    public Result index(){
        List<Orders> orders = Orders.find.all();
        return ok(views.html.orders.index.render(orders));
    }

    public Result create(Http.Request request){
        Form<Orders> orderForm = formFactory.form(Orders.class);
        return ok(views.html.orders.create.render(orderForm,request, messagesApi.preferred(request)));
    }

    public Result save(Http.Request request){
        Form<Orders> orderForm = formFactory.form(Orders.class).bindFromRequest(request);

        if(orderForm.hasErrors()){
            return badRequest(views.html.orders.create.render(orderForm,request, messagesApi.preferred(request)));
        }

        Orders order = orderForm.get();

        User user = User.find.byId(order.userId);
        if(user==null){
            return notFound("User with id "+order.userId+" not found");
        }

        Item item = Item.find.byId(order.itemId);
        if(item==null){
            return notFound("Item with id "+order.itemId+" not found");
        }
        order.fulfilled=0;

        order.stocks = new ArrayList<>();

        List<Stock> stocks = item.stocks;

        for(Stock stock : stocks ){
            if(stock.quantity>0){
                int toFulfill = order.quantity-order.fulfilled;
                if(stock.quantity>=toFulfill){
                    stock.quantity-= toFulfill;
                   order.fulfilled=order.quantity;
                   order.stocks.add(stock);
                   stock.orders.add(order);
                   stock.update();
                   break;
               } else {
                   order.fulfilled+=stock.quantity;
                   stock.quantity=0;
                   order.stocks.add(stock);
                   stock.orders.add(order);
                   stock.update();
               }
            }
        }
        if(order.fulfilled==order.quantity){
            Email email = new Email()
                    .setSubject("Order no."+order.orderId+" fulfilled")
                    .setFrom("Prodsmart <noreply@prodsmart.com>")
                    .addTo("<"+user.email+">")
                    .setBodyText("Your order for "+order.quantity+" "+item.name+"(s) has been fulfilled!");
            mailer.send(email);
        }

        try {
            order.save();
        }
        catch (Exception e) {
            return internalServerError(e.toString());
        }

        return redirect(routes.OrderController.index());
    }

    public Result edit(Integer id,Http.Request request){

        Orders order = Orders.find.byId(id);
        if(order==null){
            return notFound("Order with id "+id+" not found");
        }
        Form<Orders> orderForm = formFactory.form(Orders.class).fill(order);

        return ok(views.html.orders.edit.render(orderForm,request, messagesApi.preferred(request)));
    }

    public Result update(Http.Request request){
        Form<Orders> orderForm = formFactory.form(Orders.class).bindFromRequest(request);

        if(orderForm.hasErrors()){
            return badRequest(views.html.orders.create.render(orderForm,request, messagesApi.preferred(request)));
        }

        Orders order = orderForm.get();
        Orders oldOrder = Orders.find.byId(order.orderId);

        if(oldOrder==null){
            return notFound("Order not found");
        }

        oldOrder.orderId=order.orderId;
        oldOrder.creationDate=order.creationDate;
        oldOrder.itemId=order.itemId;
        oldOrder.quantity=order.quantity;
        oldOrder.userId=order.userId;
        oldOrder.update();
        return ok();
    }

    public Result delete(Integer id){
        Orders order = Orders.find.byId(id);
        if(order==null){
            return notFound("Order not found");
        }
        order.delete();
        return ok();
    }

    public Result show(Integer id){

        Orders order = Orders.find.byId(id);
        if(order==null){
            return notFound("Order not found");
        }

        return ok(views.html.orders.show.render(order));
    }
}
