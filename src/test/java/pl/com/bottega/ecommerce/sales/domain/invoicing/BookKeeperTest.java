package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductData;
import pl.com.bottega.ecommerce.sharedkernel.Money;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BookKeeperTest
    {
    @Mock
    InvoiceFactory invoiceFactory;
    @Mock
    Invoice invoice;
    @Mock
    InvoiceRequest invoiceRequest;
    @Mock
    TaxPolicy taxPolicy;

    BookKeeper bookKeeper;

    List<InvoiceLine> invoiceLines;
    List<RequestItem> requestItems;

    @Before
    public void setUp()
        {
        invoiceLines = new ArrayList<>();
        requestItems = new ArrayList<>();
        when(taxPolicy.calculateTax(any(), any())).thenReturn(mock(Tax.class));
        initMockitoSendRequestItemToInviteLine();
        }

    @Test
    public void simpleOneElementInItemList()
        {
        RequestItem item = makeRequestItem();
        addRequestItem(item);

        bookKeeper = new BookKeeper(invoiceFactory);
        bookKeeper.issuance(invoiceRequest, taxPolicy);

        Assert.assertThat(invoiceLines.size(), is(1));
        }




    private void initMockitoSendRequestItemToInviteLine()
        {
        when(invoiceRequest.getItems()).thenReturn(requestItems);
        when(invoiceFactory.create(any(ClientData.class))).thenReturn(invoice);
        Mockito.doAnswer((Answer) invocation ->
                {
                invoiceLines.add((InvoiceLine) invocation.getArguments()[0]);
                return null;
                }
        ).when(invoice).addItem(Matchers.any(InvoiceLine.class));
        }

    private void addRequestItem(RequestItem item)
        {
        requestItems.add(item);
        }

    private RequestItem makeRequestItem()
        {
        RequestItem item = mock(RequestItem.class);
        when(item.getProductData()).thenReturn(mock(ProductData.class));
        when(item.getTotalCost()).thenReturn(mock(Money.class));
        return item;
        }
    }