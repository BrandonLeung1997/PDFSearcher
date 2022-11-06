import fitz
import nltk
#import ocrmypdf

from .normalizer import Normalizer
from nltk.tokenize import sent_tokenize, word_tokenize
from nltk.corpus import stopwords
from nltk.stem import PorterStemmer
from ..index.index import Index


class SocketDocumentNormalizer(Normalizer):
    def __init__(self, path):
        self.__path = path
        self.__index = Index()
        self._text_normalization()

    """
    Text processing
    """

    def _text_normalization(self):
        stop_words = set(stopwords.words('english'))
        ps = PorterStemmer()

        """Text process document"""
        doc = fitz.open(self.__path)
        text = ''
        coordinated_word = list()
        for page_num, page in enumerate(doc):
            for word in page.get_text("words", sort=False):
                coordinated_word.append(word + (page_num,))
            text += page.get_text("text")

        text = text.replace('-', ' ').replace('—', ' ')
        coordinated_word = [(int(data[0]), int(data[1]), int(data[2]), int(data[3]),
                             data[4], data[5], data[6], data[7], data[8]
                             ) for data in coordinated_word]
        sentences = sent_tokenize(text)
        self.__index.set_sent_num(len(sentences))

        """Text processing sentence, word tokenlization and pos tagging"""
        tagged_tokens = []
        for index, s in enumerate(sentences):
            word_tokens = word_tokenize(s)
            sent_tagged_tokens = [(data[0], data[1], index) for data in
                                  nltk.pos_tag(word_tokens, tagset='universal')]
            tagged_tokens.append(sent_tagged_tokens)

        """
        Text processing stop word removal.
        To trip stopword and . pos word, in case matching error happen in coordinate matching
        """
        tagged_tokens_stopworded = []
        for t in tagged_tokens:
            tem = [data for data in t
                   if data[0] not in stop_words
                   and data[1] != '.'
                   and data[0].isalnum()]
            tagged_tokens_stopworded.append(tem)

        """coordinate matching"""
        fitz_word_index = 0  # index for fitz word
        coor_tokens = list()
        for s in tagged_tokens_stopworded:  # s = sentence
            coor_sent = list()
            sub_ix = 0  # index for tagged_tokens word
            while sub_ix < len(s):
                if coordinated_word[fitz_word_index][4] == s[sub_ix][0]:
                    """
                     if fitz word == to tagged_tokens word, they are the same.
                     And thus, both index of tagged tokens word and fitz word increase
                    """
                    coor_word = (s[sub_ix][0], s[sub_ix][1], s[sub_ix][2], coordinated_word[fitz_word_index][0],
                                 coordinated_word[fitz_word_index][1], coordinated_word[fitz_word_index][2],
                                 coordinated_word[fitz_word_index][3], coordinated_word[fitz_word_index][8])
                    coor_sent.append(coor_word)
                    fitz_word_index += 1
                    sub_ix += 1
                elif s[sub_ix][0] in coordinated_word[fitz_word_index][4]:
                    """
                     if fizt word != tagged_token, another possibility is that different tokenlizing method is different.
                     then we check if fizt word contain tagged_token, it ture, it is possible fizt word contain tagged_token
                     and fizt word may contain other tagged_word, so we will only increase tagged_token word index
                    """
                    coor_word = (s[sub_ix][0], s[sub_ix][1], s[sub_ix][2], coordinated_word[fitz_word_index][0],
                                 coordinated_word[fitz_word_index][1], coordinated_word[fitz_word_index][2],
                                 coordinated_word[fitz_word_index][3], coordinated_word[fitz_word_index][8])
                    coor_sent.append(coor_word)
                    sub_ix += 1
                else:
                    """ if fizt word not contain tagged_token word, we move to the next fizt word"""
                    fitz_word_index += 1
            coor_tokens.append(coor_sent)

        """Text processing lowercase, stemming"""
        normalized_sentences = []
        for t in coor_tokens:
            tem = [(ps.stem(data[0].lower()), data[1], data[2], data[3], data[4], data[5], data[6], data[7])
                   for data in t
                   if not data[0].isnumeric()]
            normalized_sentences.append(tem)

        """ add word to index object"""
        for n_s in normalized_sentences:
            for n_t in n_s:
                self.__index.add_word(stemmed_word=n_t[0], pos=n_t[1], sent_num=n_t[2],
                                      x1=n_t[3], y1=n_t[4], x2=n_t[5], y2=n_t[6], page=n_t[7])

    def get_index(self):
        return self.__index
