package com.example.baseball.repository.impl;

import com.example.baseball.domain.Post;
import com.example.baseball.domain.QPostLike;
import com.example.baseball.repository.PostDetailRepository;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static com.example.baseball.domain.QMember.member;
import static com.example.baseball.domain.QPost.post;
import static com.example.baseball.domain.QPostLike.postLike;
import static com.example.baseball.domain.QTeam.team;

public class PostDetailRepositoryImpl implements PostDetailRepository {

    private final JPAQueryFactory queryFactory;

    public PostDetailRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<Post> selectPostListByTeam(String searchText, String teamId, Pageable pageable) {
        List<Post> results = queryFactory.select(post)
                .from(post)
                .join(post.author, member).fetchJoin()
                .join(post.followedTeam, team).fetchJoin()
                .leftJoin(post.postLikes, postLike).fetchJoin()
                .where(post.followedTeam.teamId.eq(teamId)
                        .and(post.isUse.isTrue())
                        .and(post.title.contains(searchText)))
                .orderBy(getSortedOrder(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long resultsCount = queryFactory.select(post.count())
                .from(post)
                .where(post.followedTeam.teamId.eq(teamId)
                        .and(post.content.contains(searchText)))
                .fetchOne();

        return new PageImpl<>(results, pageable, resultsCount);
    }

    @Override
    public Post findByPostId(Long postId) {
        return queryFactory.select(post)
                .from(post)
                .join(post.author, member).fetchJoin()
                .join(post.followedTeam, team).fetchJoin()
                .leftJoin(post.postLikes, postLike).fetchJoin()
                .where(post.postId.eq(postId)
                        .and(post.isUse.isTrue()))
                .fetchOne();
    }

    // Sort 객체를 OrderSpecifier로 변환
    private OrderSpecifier<?>[] getSortedOrder(Sort sort) {
        return sort.stream()
                .map(order -> {
                    PathBuilder pathBuilder = new PathBuilder(post.getType(), post.getMetadata());
                    return new OrderSpecifier(
                            order.isAscending() ? Order.ASC : Order.DESC,
                            pathBuilder.get(order.getProperty())
                    );
                })
                .toArray(OrderSpecifier[]::new);
    }
}
