package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
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
    InvoiceFactory mockInvoiceFactory;
    @Mock
    TaxPolicy mockTaxPolicy;
    @Mock
    Tax mockTax;

    InvoiceRequest invoiceRequest;
    BookKeeper testedBookKeeper;
    Invoice invoice;

    @Before
    public void setUp()
        {
        invoiceRequest =new InvoiceRequest(mock(ClientData.class));
        invoice = new Invoice(Id.generate(), mock(ClientData.class));
        when(mockInvoiceFactory.create(any(ClientData.class))).thenReturn(invoice);

        setTaxAmountForAllInvoiceLine(10);
        }

    @Test
    public void simpleOneElementInItemList()
        {
        RequestItem item = makeRequestItem(1, 2);
        addRequestItem(item);

        testedBookKeeper = new BookKeeper(mockInvoiceFactory);
        testedBookKeeper.issuance(invoiceRequest, mockTaxPolicy);

        Assert.assertThat(invoice.getItems().size(), is(1));
        }

    @Test
    public void simpleElementsInItemList()
        {
        addRequestItem(makeRequestItem());
        addRequestItem(makeRequestItem(2,2));

        testedBookKeeper = new BookKeeper(mockInvoiceFactory);
        testedBookKeeper.issuance(invoiceRequest, mockTaxPolicy);

        Assert.assertThat(invoice.getItems().size(), is(2));
        }

    @Test
    public void oneElementOneCalculateTax()
        {
        addRequestItem(makeRequestItem());

        testedBookKeeper = new BookKeeper(mockInvoiceFactory);
        testedBookKeeper.issuance(invoiceRequest, mockTaxPolicy);

        verify(mockTaxPolicy, times(1)).calculateTax(any(), any());
        }

    @Test
    public void AFewElementsAFewCalculateTaxInvocation()
        {
        addRequestItem(makeRequestItem());
        addRequestItem(makeRequestItem());

        testedBookKeeper = new BookKeeper(mockInvoiceFactory);
        testedBookKeeper.issuance(invoiceRequest, mockTaxPolicy);

        verify(mockTaxPolicy, times(2)).calculateTax(any(), any());
        }

    @Test
    public void xElementsXCalculateTaxInvocation()
        {
        int thisIsTheX = 100;
        for (int i = 0; i < thisIsTheX; i++)
            {
            addRequestItem(makeRequestItem());
            }

        testedBookKeeper = new BookKeeper(mockInvoiceFactory);
        testedBookKeeper.issuance(invoiceRequest, mockTaxPolicy);

        verify(mockTaxPolicy, times(thisIsTheX)).calculateTax(any(), any());
        }

    private void addRequestItem(RequestItem item)
        {
        invoiceRequest.add(item);
        }

    private RequestItem makeRequestItem(Integer quantity, Integer money)
        {
        return new RequestItem(mock(ProductData.class), quantity, new Money(money));
        }

    private RequestItem makeRequestItem()
        {
        return new RequestItem(mock(ProductData.class), 1, new Money(1));
        }

    private void setTaxAmountForAllInvoiceLine(Integer money)
        {
        when(mockTaxPolicy.calculateTax(any(), any())).thenReturn(mockTax);
        when(mockTax.getAmount()).thenReturn(new Money(money));
        }
    }