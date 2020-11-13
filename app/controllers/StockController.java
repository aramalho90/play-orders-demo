package controllers;
import models.*;
import play.data.Form;
import play.data.FormFactory;
import play.i18n.MessagesApi;
import play.libs.mailer.Email;
import play.api.libs.mailer.MailerClient;
import play.mvc.*;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class StockController extends Controller {
    private final MailerClient mailer;

    FormFactory formFactory;
    MessagesApi messagesApi;
    @Inject
    public StockController(MailerClient mailer, FormFactory formFactory, MessagesApi messagesApi) {
        this.mailer = mailer;
        this.formFactory=formFactory;
        this.messagesApi=messagesApi;
    }

    public Result index(){
        List<Stock> stocks = Stock.find.all();
        return ok(views.html.stocks.index.render(stocks));
    }

    public Result create(Http.Request request){
        Form<Stock> stockForm = formFactory.form(Stock.class);
        return ok(views.html.stocks.create.render(stockForm,request, messagesApi.preferred(request)));
    }

    public Result save(Http.Request request){
        Form<Stock> stockForm = formFactory.form(Stock.class).bindFromRequest(request);

        if(stockForm.hasErrors()){
            return badRequest(views.html.stocks.create.render(stockForm,request, messagesApi.preferred(request)));
        }

        Stock stock = stockForm.get();

        Item item = Item.find.byId(stock.itemId);
        if(item==null){
            return notFound("Item with id "+stock.itemId+" not found");
        }

        stock.orders = new ArrayList<>();

        List<Orders> orders = item.orders;
        for(Orders order : orders ) {

        if (order.fulfilled<order.quantity){

            int toFulfill=order.quantity-order.fulfilled;

            if(stock.quantity>=toFulfill){
                stock.quantity-= toFulfill;
                order.fulfilled=order.quantity;
                order.stocks.add(stock);
                stock.orders.add(order);
                order.update();

                User user = User.find.byId(order.userId);
                Email email = new Email()
                        .setSubject("Order no."+order.orderId+" fulfilled")
                        .setFrom("Prodsmart <noreply@prodsmart.com>")
                        .addTo("<"+user.email+">")
                        .setBodyText("Your order for "+order.quantity+" "+item.name+"(s) has been fulfilled!");
                mailer.send(email);

                break;
                }
            else {
                order.fulfilled+=stock.quantity;
                stock.quantity=0;
                order.stocks.add(stock);
                stock.orders.add(order);
                order.update();
                }

            }
        }

        try {
            stock.save();
        }
        catch (Exception e) {
            return internalServerError(e.toString());
        }

        return redirect(routes.StockController.index());
    }

    public Result edit(Integer id,Http.Request request){

        Stock stock = Stock.find.byId(id);
        if(stock==null){
            return notFound("Stock with id "+id+" not found");
        }
        Form<Stock> stockForm = formFactory.form(Stock.class).fill(stock);

        return ok(views.html.stocks.edit.render(stockForm,request, messagesApi.preferred(request)));
    }

    public Result update(Http.Request request){
        Form<Stock> stockForm = formFactory.form(Stock.class).bindFromRequest(request);

        if(stockForm.hasErrors()){
            return badRequest(views.html.stocks.create.render(stockForm,request, messagesApi.preferred(request)));
        }

        Stock stock = stockForm.get();
        Stock oldStock = Stock.find.byId(stock.stockId);

        if(oldStock==null){
            return notFound("Stock not found");
        }

        oldStock.stockId=stock.stockId;
        oldStock.creationDate=stock.creationDate;
        oldStock.itemId=stock.itemId;
        oldStock.quantity=stock.quantity;

        oldStock.update();
        return ok();
    }

    public Result delete(Integer id){
        Stock stock = Stock.find.byId(id);
        if(stock==null){
            return notFound("Stock not found");
        }
        stock.delete();
        return ok();
    }

    public Result show(Integer id){

        Stock stock = Stock.find.byId(id);
        if(stock==null){
            return notFound("Stock not found");
        }

        return ok(views.html.stocks.show.render(stock));
    }
}
