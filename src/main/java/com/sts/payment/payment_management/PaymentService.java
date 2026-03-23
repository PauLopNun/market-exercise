package com.sts.payment.payment_management;

import com.sts.payment.data_access.CartItem;
import com.sts.payment.data_access.User;
import com.sts.shared.model.Product;
import java.util.List;

public class PaymentService {
    
    private final User user;
    private final PaymentDataGateway dataGateway;
    
    public PaymentService(User user, PaymentDataGateway dataGateway) {
        this.user = user;
        this.dataGateway = dataGateway;
    }
    
    /**
     * Procesa el pago del carrito del usuario.
     * Si el budget es insuficiente, elimina items LIFO hasta poder pagar.
     * 
     * @return true si el pago fue exitoso, false si falló
     */
    public boolean checkout() {
        try {
            List<CartItem> cart = getCartByUserId();
            
            if (isCartEmpty(cart)) {
                return true;
            }
            
            double totalPrice = calculateTotalPrice(cart);
            
            if (isBudgetInsufficient(totalPrice)) {
                handleInsufficientBudget();
                cart = getCartByUserId();
                totalPrice = calculateTotalPrice(cart);
                
                if (isCartEmpty(cart)) {
                    return true;
                }
            }
            
            if (!verifyBudgetSufficiency(totalPrice)) {
                return false;
            }
            
            double newBudget = deductPaymentFromBudget(totalPrice);
            incrementProductStocksFromCart(cart);
            clearUserCart();
            
            logSuccessfulPayment(totalPrice, newBudget);
            return true;
            
        } catch (Exception e) {
            System.err.println("✗ Error durante el checkout: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 1. Obtener carrito actual del usuario
     */
    private List<CartItem> getCartByUserId() {
        return dataGateway.getCartByUserId(user.getId());
    }
    
    /**
     * Si el carrito está vacío, no hay nada que cobrar
     */
    private boolean isCartEmpty(List<CartItem> cart) {
        if (cart.isEmpty()) {
            System.out.println("✓ Carrito vacío. Sin cargos.");
            return true;
        }
        return false;
    }
    
    /**
     * 2. Calcular el precio total del carrito
     */
    private boolean isBudgetInsufficient(double totalPrice) {
        return user.getBudget() < totalPrice;
    }
    
    /**
     * 3. Si el budget es insuficiente, eliminar items LIFO hasta poder pagar
     */
    private void handleInsufficientBudget() {
        System.out.println("⚠ Budget insuficiente ($" + String.format("%.2f", user.getBudget()) + 
                         "). Eliminando items del carrito...");
        removeCartLIFO();
    }
    
    /**
     * 4. Verificar nuevamente que hay suficiente budget
     */
    private boolean verifyBudgetSufficiency(double totalPrice) {
        if (user.getBudget() < totalPrice) {
            System.err.println("✗ Budget insuficiente incluso después de eliminar items. " +
                             "Budget: $" + String.format("%.2f", user.getBudget()) + 
                             ", Total: $" + String.format("%.2f", totalPrice));
            return false;
        }
        return true;
    }
    
    /**
     * 5. Procesar el pago: deducir monto del budget
     */
    private double deductPaymentFromBudget(double totalPrice) {
        double newBudget = user.getBudget() - totalPrice;
        dataGateway.updateBalance(user.getId(), newBudget);
        return newBudget;
    }
    
    /**
     * 6. Incrementar el stock de los productos vendidos
     */
    private void incrementProductStocksFromCart(List<CartItem> cart) {
        for (CartItem entry : cart) {
            dataGateway.incrementProductStock(entry.getProductId(), entry.getQuantity());
        }
    }
    
    /**
     * 7. Limpiar el carrito después del pago exitoso
     */
    private void clearUserCart() {
        dataGateway.clearCart(user.getId());
    }
    
    /**
     * Registrar el pago exitoso con detalles
     */
    private void logSuccessfulPayment(double totalPrice, double newBudget) {
        System.out.println("✓ Pago procesado exitosamente. " +
                         "Monto: $" + String.format("%.2f", totalPrice) + 
                         ", Nuevo balance: $" + String.format("%.2f", newBudget));
    }
    
    /**
     * Elimina items del carrito LIFO (Last In First Out) mientras el budget sea insuficiente.
     * Continúa eliminando el último item hasta que el presupuesto sea suficiente.
     */
    public void removeCartLIFO() {
        try {
            // Obtener el carrito del usuario
            List<CartItem> cart = dataGateway.getCartByUserId(user.getId());
            
            // Calcular el precio total actual del carrito
            double totalPrice = calculateTotalPrice(cart);
            
            // Mientras el budget sea menor al precio total y haya items en el carrito
            while (user.getBudget() < totalPrice && !cart.isEmpty()) {
                CartItem lastItem = cart.get(cart.size() - 1);
                
                // Eliminar el último item del carrito (LIFO)
                dataGateway.removeLastItemFromCart(user.getId());
                
                System.out.println("  ⊝ Eliminado: " + lastItem.getProductId() + 
                                 " (cantidad: " + lastItem.getQuantity() + ")");
                
                // Actualizar la lista del carrito
                cart = dataGateway.getCartByUserId(user.getId());
                
                // Recalcular el precio total
                totalPrice = calculateTotalPrice(cart);
            }
            
            if (cart.isEmpty()) {
                System.out.println("  ⊝ Carrito completamente vaciado");
            } else {
                System.out.println("  ✓ Balance suficiente. Total carrito: $" + String.format("%.2f", totalPrice));
            }
            
        } catch (Exception e) {
            System.err.println("Error en removeCartLIFO: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Calcula el precio total del carrito multiplicando cantidad * precio de cada producto.
     * 
     * @param cart lista de items del carrito
     * @return precio total del carrito
     */
    private double calculateTotalPrice(List<CartItem> cart) {
        double total = 0.0;
        for (CartItem entry : cart) {
            Product product = dataGateway.getProductById(entry.getProductId());
            if (product != null) {
                double itemTotal = product.getPrice() * entry.getQuantity();
                total += itemTotal;
            }
        }
        return total;
    }
}
