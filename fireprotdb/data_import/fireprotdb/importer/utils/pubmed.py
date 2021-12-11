import re
from Bio import Entrez


def _do_mapping(batch):
    pubs = {}


def map_pubmed_ids(ids, batch_size=200):
    Entrez.email='connie.wang@protabit.com'

    pubs = {}
    for i in range(int((len(ids)+(batch_size-1)) / batch_size)):
        index_from = i * batch_size
        index_to = (i + 1) * batch_size if (i + 1) * batch_size < len(ids) else len(ids)
        handle = Entrez.efetch(db="pubmed", id=ids[index_from:index_to], retmode='xml')
        record = Entrez.read(handle)
        article_list = record['PubmedArticle']
        for article in article_list:
            pmid = str(article['MedlineCitation']['PMID'])
            pub = {}
            pubs[pmid] = pub
            for aid in article['PubmedData']['ArticleIdList']:
                pub[aid.attributes["IdType"]] = str(aid)
            article_details = article['MedlineCitation']['Article']
            pub['title'] = article_details['ArticleTitle']
            pub['journal'] = article_details['Journal']['Title']
            pub['volume'] = article_details['Journal']['JournalIssue']['Volume'] if 'Volume' in \
                                                                                    article_details['Journal'][
                                                                                        'JournalIssue'] else None
            pub['issue'] = article_details['Journal']['JournalIssue']['Issue'] if 'Issue' in article_details['Journal'][
                'JournalIssue'] else None
            pub_date = article_details['Journal']['JournalIssue']['PubDate']
            if 'Year' in pub_date:
                pub['year'] = pub_date['Year']
            elif 'MedlineDate' in pub_date:
                if m := re.match('^(\\d{4})\\s+', pub_date['MedlineDate']):
                    pub['year'] = m.group(1)
                else:
                    print("Cannot parse medline date for publication {}!".format(pmid))
            else:
                print("Publication {} has no year!".format(pmid))
            try:
                pub['pages'] = article_details['Pagination']['MedlinePgn']
            except:
                pass
            pub['authors'] = []
            for author in article_details['AuthorList']:
                pub['authors'].append(
                    {
                        'last_name': author['LastName'],
                        'fore_name': author['ForeName'],
                        'initials': author['Initials'],
                    }
                )
    return pubs

def explodepub(type, p):
    title = p['title']
    journal = p['journal']
    volume = p['volume']
    issue = p['issue']
    year = p['year']
    pages = p['pages'] if 'pages' in p else None
    doi = p['doi'] if 'doi' in p else None
    pmid = p['pubmed'] if 'pubmed' in p else None

    if type == "pub":
        return (pmid, doi, title, journal, volume, issue, year, pages)
    elif type == "pubid":
        return (pmid, doi)
    else:
        return None