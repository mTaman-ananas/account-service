package com.siriusxi.hexarch.account.adapter.in.web;

import com.siriusxi.hexarch.account.core.domain.Product;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.text.NumberFormat.getInstance;
import static java.time.format.DateTimeFormatter.ofLocalizedDate;
import static java.time.format.FormatStyle.FULL;

@Controller
public class ProductsController {

    @GetMapping("/index")
    public ModelAndView index() {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");

        List<Product> products = fetchProducts();
        modelAndView.addObject("products", products);

        return modelAndView;
    }

    private List<Product> fetchProducts() {
        Locale locale = LocaleContextHolder.getLocale();

        List<Product> products = new ArrayList<>();

        Product product = new Product();
        product.setName("prod.tv");
        product.setPrice(localizePrice(locale, 15678.43));
        product.setLastUpdated(localizeDate(locale, LocalDate.of(2021, Month.SEPTEMBER, 22)));
        products.add(product);

        product = new Product();
        product.setName("prod.wm");
        product.setPrice(localizePrice(locale, 152637.76));
        product.setLastUpdated(localizeDate(locale, LocalDate.of(2021, Month.SEPTEMBER, 20)));
        products.add(product);

        product = new Product();
        product.setName("prod.frdg");
        product.setPrice(localizePrice(locale, 162547.53));
        product.setLastUpdated(localizeDate(locale, LocalDate.of(2022, Month.OCTOBER, 10)));
        products.add(product);

        product = new Product();
        product.setName("prod.cooker");
        product.setPrice(localizePrice(locale, 113548.33));
        product.setLastUpdated(localizeDate(locale, LocalDate.of(2023, Month.JANUARY, 2)));
        products.add(product);

        return products;
    }

    private String localizeDate(final Locale locale, final LocalDate date) {
        return ofLocalizedDate(FULL)
                .withLocale(locale)
                .format(date);
    }

    private String localizePrice(final Locale locale, final Double price) {
        return getInstance(locale).format(price);
    }

}
