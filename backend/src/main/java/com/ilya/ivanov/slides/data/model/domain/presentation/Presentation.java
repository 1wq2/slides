package com.ilya.ivanov.slides.data.model.domain.presentation;

import com.google.common.collect.Lists;
import com.ilya.ivanov.slides.data.model.domain.user.User;
import com.ilya.ivanov.slides.data.model.dto.presentation.PresentationDto;
import lombok.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.IndexedEmbedded;

import javax.persistence.*;
import java.util.Collection;

import static com.ilya.ivanov.slides.constants.SearchConstants.defaultAnalyzerName;
import static java.util.stream.Collectors.toList;

/**
 * Created by i.ivanov on 11/23/17.
 */
@Entity
@Table(name = "presentation")
@Indexed
@Data
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public final class Presentation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    @NonNull
    @Field
    @Analyzer(definition = defaultAnalyzerName)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "owner_id")
    @IndexedEmbedded
    @Analyzer(definition = defaultAnalyzerName)
    private User owner;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Collection<Slide> slides = Lists.newArrayList();

    @OneToOne(cascade = CascadeType.ALL)
    private ShareLink shareLink;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @CollectionTable(name = "tags", joinColumns = @JoinColumn(name = "tag_id"))
    @IndexedEmbedded
    @Field
    @Analyzer(definition = defaultAnalyzerName)
    private Collection<String> tags = Lists.newArrayList();

    private Presentation(Long id, @NonNull String title, @NonNull Collection<String> tags) {
        this(title);
        this.id = id;
        this.tags = tags;
    }

    public Presentation merge(PresentationDto presentationDto) {
        this.title = presentationDto.getTitle();
        this.tags = presentationDto.getTags();
        if (this.shareLink != null) {
            this.shareLink.merge(presentationDto.getShareLink());
        }
        return this;
    }

    public PresentationDto toDto() {
        val id = this.getId();
        val owner = this.getOwner().getUsername();
        val title = this.getTitle();
        val slidesIds = this.getSlides().stream().map(Slide::getId).collect(toList());
        val shareLink = this.getShareLink();
        String link = shareLink != null ? shareLink.getLink() : null;
        val tags = this.getTags();
        return new PresentationDto(id, owner, title, tags, slidesIds, link);
    }

    public static Presentation fromDto(PresentationDto presentationDto) {
        val id = presentationDto.getId();
        val title = presentationDto.getTitle();
        val tags = presentationDto.getTags();
        return new Presentation(id, title, tags);
    }
}
