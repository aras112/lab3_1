package pl.com.bottega.ecommerce.sales.domain.invoicing;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
    InvoiceFactory mockedInvoiceFactory;
    @Mock
    TaxPolicy mockedTaxPolicy;
    @Mock
    Tax mockedTax;

    InvoiceRequest invoiceRequest;
    BookKeeper testedBookKeeper;
    Invoice invoice;
    private final int DEFAULT_TAX = 10;

    @Before
    public void setUp()
        {
        invoiceRequest = new InvoiceRequest(mock(ClientData.class));
        invoice = new Invoice(Id.generate(), mock(ClientData.class));

        when(mockedInvoiceFactory.create(any(ClientData.class))).thenReturn(invoice);

        setDefaultTaxAmountForAllInvoiceLine(DEFAULT_TAX);
        }

    @Test
    public void simpleOneElementInItemList()
        {
        RequestItem item = makeRequestItem();
        addRequestItem(item);

        testedBookKeeper = new BookKeeper(mockedInvoiceFactory);
        testedBookKeeper.issuance(invoiceRequest, mockedTaxPolicy);

        Assert.assertThat(invoice.getItems().size(), is(1));
        }

    @Test
    public void simpleElementsInItemList()
        {
        addRequestItem(makeRequestItem());
        addRequestItem(makeRequestItem());

        testedBookKeeper = new BookKeeper(mockedInvoiceFactory);
        testedBookKeeper.issuance(invoiceRequest, mockedTaxPolicy);

        Assert.assertThat(invoice.getItems().size(), is(2));
        }

    @Test
    public void oneElementOneCalculateTax()
        {
        addRequestItem(makeRequestItem());

        testedBookKeeper = new BookKeeper(mockedInvoiceFactory);
        testedBookKeeper.issuance(invoiceRequest, mockedTaxPolicy);

        verify(mockedTaxPolicy, times(1)).calculateTax(any(), any());
        }

    @Test
    public void AFewElementsAFewCalculateTaxInvocation()
        {
        addRequestItem(makeRequestItem());
        addRequestItem(makeRequestItem());

        testedBookKeeper = new BookKeeper(mockedInvoiceFactory);
        testedBookKeeper.issuance(invoiceRequest, mockedTaxPolicy);

        verify(mockedTaxPolicy, times(2)).calculateTax(any(), any());
        }

    @Test
    public void xElementsXCalculateTaxInvocation()
        {
        int thisIsTheX = 100;
        for (int i = 0; i < thisIsTheX; i++)
            {
            addRequestItem(makeRequestItem());
            }

        testedBookKeeper = new BookKeeper(mockedInvoiceFactory);
        testedBookKeeper.issuance(invoiceRequest, mockedTaxPolicy);

        verify(mockedTaxPolicy, times(thisIsTheX)).calculateTax(any(), any());
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

    private void setDefaultTaxAmountForAllInvoiceLine(Integer money)
        {
        when(mockedTaxPolicy.calculateTax(any(), any())).thenReturn(mockedTax);
        when(mockedTax.getAmount()).thenReturn(new Money(money));
        }
    }