package gift.entity;

import gift.entity.vo.WishList;
import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "member")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @Column(name = "kakao_access_token")
    private String kakaoAccessToken;

    @Column(name = "kakao_refresh_token")
    private String kakaoRefreshToken;

    @Embedded
    private WishList wishList = new WishList();

    protected Member() {}

    public Member(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getKakaoAccessToken() { return kakaoAccessToken; }
    public String getKakaoRefreshToken() { return kakaoRefreshToken; }

    public void update(String newEmail, String newEncodedPassword) {
        if (newEmail != null && !newEmail.isBlank()) {
            this.email = newEmail;
        }
        if (newEncodedPassword != null && !newEncodedPassword.isBlank()) {
            this.password = newEncodedPassword;
        }
    }

    public void addWish(Product product) {
        wishList.add(this, product);
    }

    public void removeWish(Product product) {
        wishList.remove(product);
    }

    public List<Product> getWishes() {
        return wishList.getProducts();
    }

    public void setKakaoAccessToken(String kakaoAccessToken) {
        this.kakaoAccessToken = kakaoAccessToken;
    }

    public void setKakaoRefreshToken(String kakaoRefreshToken) {
        this.kakaoRefreshToken = kakaoRefreshToken;
    }
}