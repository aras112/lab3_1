package pl.com.bottega.ecommerce.sales.application.api.handler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.Instant;
import java.util.Date;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.ClientData;
import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductType;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.sharedkernel.Money;
import pl.com.bottega.ecommerce.system.application.SystemContext;
import pl.com.bottega.ecommerce.system.application.SystemUser;

import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddProductCommandHandlerTest
    {
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private SuggestionService suggestionService;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private SystemContext systemContext;
    @Mock
    private Product product;
    @Mock
    private AddProductCommand command;

    public AddProductCommandHandler testedHandler;
    private Reservation reservation;

    @Before
    public void setUp()
        {
        makeDefaultReservation();
        initAllStuff();
        }

    @Test
    public void simpleTestOneProductTwoIsAvailableInvocation()
        //Reservation class inv. isAvailable()  too!!!
    {
    testedHandler = new AddProductCommandHandler();
    Product product = makeMockProductAndSetProductAvailable(true);
    setReturningProductForProductRepo(product);
    whenForTestedHandler();
    verify(product, times(2)).isAvailable();
    }

    @Test
    public void productIsAvailableAndIsAddedToReservation()
        {
        testedHandler = new AddProductCommandHandler();
        Product product = makeMockProductAndSetProductAvailable(true);
        setReturningProductForProductRepo(product);
        whenForTestedHandler();
        Assert.assertThat(product, is(getProductFromReservation(getFinallySavedReservations())));
        }

    @Test
    public void productIsNOTAvailableAndIsNOTAddedToReservation()
        {
        testedHandler = new AddProductCommandHandler();
        Product newNotAvailableProduct = makeMockProductAndSetProductAvailable(false);
        setReturningProductForProductRepo(newNotAvailableProduct);
        setReturningProductForSuggestion(makeMockProductAndSetProductAvailable(true));
        whenForTestedHandler();
        Assert.assertNotEquals(newNotAvailableProduct, getProductFromReservation(getFinallySavedReservations()));
        }


    private void whenForTestedHandler()
        {
        setAllClassesForTestedHandler();
        testedHandler.handle(command);
        }

    private Product makeMockProductAndSetProductAvailable(Boolean isAvailable)
        {
        Product product = mock(Product.class);
        when(product.isAvailable()).thenReturn(isAvailable);
        return product;
        }

    private Product makeRealProductAndSetProductAvailable(Boolean isAvailable, Integer money,
                                                          String name)
        {
        Product product = new Product(Id.generate(), new Money(money), name, ProductType.DRUG);
        product = spy(product);// useful spy
        when(product.isAvailable()).thenReturn(isAvailable);
        return product;
        }


    private void setAllClassesForTestedHandler()
        {
        testedHandler.setClientRepository(clientRepository);
        testedHandler.setProductRepository(productRepository);
        testedHandler.setReservationRepository(reservationRepository);
        testedHandler.setSystemContext(systemContext);
        testedHandler.setSuggestionService(suggestionService);
        }

    private void setReturningProductForProductRepo(Product product)
        {
        when(productRepository.load(any(Id.class))).thenReturn(product);
        }

    private void setReturningProductForSuggestion(Product product)
        {
        when(suggestionService.suggestEquivalent(any(), any())).thenReturn(product);
        }

    private void initAllStuff()
        {
        when(reservationRepository.load(any(Id.class))).thenReturn(reservation);
        when(suggestionService.suggestEquivalent(any(), any())).thenReturn(product);
        when(clientRepository.load(any(Id.class))).thenReturn(mock(Client.class));
        when(systemContext.getSystemUser()).thenReturn(mock(SystemUser.class));
        }

    private Reservation getFinallySavedReservations()
        {
        ArgumentCaptor<Reservation> reservationArgumentCaptor = ArgumentCaptor.forClass(Reservation.class);
        verify(reservationRepository).save(reservationArgumentCaptor.capture());
        return reservationArgumentCaptor.getValue();
        }

    private void makeDefaultReservation()
        {
        reservation = new Reservation(Id.generate(), Reservation.ReservationStatus.OPENED,
                mock(ClientData.class), Date.from(Instant.now()));
        }

    private Product getProductFromReservation(Reservation reservation)
        {
        return reservation.getItems().get(0).getProduct();
        }
    }