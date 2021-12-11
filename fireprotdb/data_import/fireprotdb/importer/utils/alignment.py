import re

from Bio import pairwise2

class Alignment:

    def __init__(self, seqA, seqB):
        self.seqA = seqA
        self.seqB = seqB

    def one_two(self, index):
        return self._map(self.seqA, self.seqB, index)

    def two_one(self, index):
        return self._map(self.seqB, self.seqA, index)

    @staticmethod
    def _map(a, b, index):
        j = 0
        for i in range(len(a)):
            if a[i] != '-':
                j += 1
            if j == index:
                break
        j = 0
        for k in range(i + 1):
            if b[k] != '-':
                j += 1

        return None if j == 0 or b[k] == '-' else j


def align(seqA, seqB):
    alignments = pairwise2.align.globalxs(seqA, seqB, -.99, -.7)
    best_aln = alignments[0]
    if len(alignments) > 1:
        gap_count = [re.sub('[-]*$', '', re.sub('^[-]*', '', a.seqA)).count('-') for a in alignments]

        min_gap_count = min(gap_count)
        same_gap_count = gap_count.count(min_gap_count)

        if same_gap_count > 1:
            print("Multiple candidate alignments detected...selecting first")
            for i in range(len(alignments)):
                if gap_count[i] == min_gap_count:
                    best_aln = alignments[i]
        else:
            best_aln = alignments[gap_count.index(min_gap_count)]

    return Alignment(best_aln.seqA, best_aln.seqB)