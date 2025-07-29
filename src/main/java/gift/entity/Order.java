package gift.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private Option option;

    @Column(nullable = false)
    private int quantity;

    private String message;

    @Column(nullable = false)
    private LocalDateTime orderDateTime;

    protected Order() {}

    public Order(Member member, Option option, int quantity, String message) {
        this.member = member;
        this.option = option;
        this.quantity = quantity;
        this.message = message;
        this.orderDateTime = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Member getMember() { return member; }
    public Option getOption() { return option; }
    public int getQuantity() { return quantity; }
    public String getMessage() { return message; }
    public LocalDateTime getOrderDateTime() { return orderDateTime; }
}
