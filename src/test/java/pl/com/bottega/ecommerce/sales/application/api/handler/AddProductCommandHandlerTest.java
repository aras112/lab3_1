package pl.com.bottega.ecommerce.sales.application.api.handler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pl.com.bottega.ecommerce.canonicalmodel.publishedlanguage.Id;
import pl.com.bottega.ecommerce.sales.application.api.command.AddProductCommand;
import pl.com.bottega.ecommerce.sales.domain.client.Client;
import pl.com.bottega.ecommerce.sales.domain.client.ClientRepository;
import pl.com.bottega.ecommerce.sales.domain.equivalent.SuggestionService;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.Product;
import pl.com.bottega.ecommerce.sales.domain.productscatalog.ProductRepository;
import pl.com.bottega.ecommerce.sales.domain.reservation.Reservation;
import pl.com.bottega.ecommerce.sales.domain.reservation.ReservationRepository;
import pl.com.bottega.ecommerce.system.application.SystemContext;
import pl.com.bottega.ecommerce.system.application.SystemUser;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
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
    private Reservation reservation;
    @Mock
    private Product product;
    @Mock
    private AddProductCommand command;

    AddProductCommandHandler testedHandler;

    @Before
    public void setUp()
        {
        when(reservationRepository.load(any(Id.class))).thenReturn(reservation);
        when(productRepository.load(any(Id.class))).thenReturn(product);
        when(suggestionService.suggestEquivalent(any(), any())).thenReturn(product);
        when(clientRepository.load(any(Id.class))).thenReturn(mock(Client.class));
        when(systemContext.getSystemUser()).thenReturn(mock(SystemUser.class));
        }

    @Test
    public void test()
        {
        testedHandler=new AddProductCommandHandler();
        setAllClassesForTestedHandler();
        testedHandler.handle(command);
        }

    private Product makeProductAndSetAvailable(Boolean isAvailable)
        {
        Product product = mock(Product.class);
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
    }