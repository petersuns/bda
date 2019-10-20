#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdint.h>
#include "dataset.h"
#include "output.h"
#include "find_frequent_pairs.h"

int
document_has_word(const dataset *ds, size_t doc_index, size_t voc_index)
{
    // Auxiliary function for `find_pairs_naive_bitmaps`

    uint8_t *column_ptr = get_term_bitmap(ds, voc_index);

    if (doc_index >= ds->num_documents)
    {
        printf("error: doc_index out of bounds %ld/%ld\n", doc_index, ds->num_documents);
        exit(1);
    }

    size_t byte_i = doc_index / 8;
    size_t bit_i = 7 - (doc_index % 8);

    uint8_t b = column_ptr[byte_i];
    return ((b >> bit_i) & 0x1) ? 1 : 0;
}

void
find_pairs_naive_bitmaps(const dataset *ds, output_pairs *op, int threshold)
{
    // This is an example implementation. You don't need to change this, you
    // should implement `find_pairs_quick_*`.

    for (size_t t1 = 0; t1 < ds->vocab_size; ++t1)
    {
        for (size_t t2 = t1+1; t2 < ds->vocab_size; ++t2)
        {
            int count = 0;
            for (size_t d = 0; d < ds->num_documents; ++d)
            {
                int term1_appears_in_doc = document_has_word(ds, d, t1);
                int term2_appears_in_doc = document_has_word(ds, d, t2);
                if (term1_appears_in_doc && term2_appears_in_doc)
                {
                    ++count;
                }
            }
            if (count >= threshold)
                push_output_pair(op, t1, t2, count);
        }
    }
}

void
find_pairs_naive_indexes(const dataset *ds, output_pairs *op, int threshold)
{
    // This is an example implementation. You don't need to change this, you
    // should implement `find_pairs_quick_*`.

    for (size_t t1 = 0; t1 < ds->vocab_size; ++t1)
    {
        const index_list *il1 = get_term_indexes(ds, t1);
        for (size_t t2 = t1+1; t2 < ds->vocab_size; ++t2)
        {
            const index_list *il2 = get_term_indexes(ds, t2);
            
            int count = 0;
            size_t i1 = 0, i2 = 0;
            for (; i1 < il1->len && i2 < il2->len;)
            {
                size_t x1 = il1->indexes[i1], x2 = il2->indexes[i2];
                if (x1 == x2) { ++count; ++i1; ++i2; }
                else if (x1 < x2) { ++i1; }
                else { ++i2; }
            }

            if (count >= threshold)
                push_output_pair(op, t1, t2, count);
        }
    }
}


void
find_pairs_quick_bitmaps(const dataset *ds, output_pairs *op, int threshold)
{
    // TODO implement a quick `find_pairs_quick_bitmaps` procedure using
    // `get_term_bitmap`.

    for (size_t t1 = 0; t1 < ds->vocab_size; ++t1)
    {
        for (size_t t2 = t1+1; t2 < ds->vocab_size; ++t2)
        {
            int count = 0;
            for (size_t d = 0; d < ds->num_documents; ++d)
            {
                if (document_has_word(ds, d, t1) && document_has_word(ds, d, t2))
                {
                    ++count;
                }
            }
            if (count >= threshold)
                push_output_pair(op, t1, t2, count);
        }
    }
}

void
find_pairs_quick_indexes(const dataset *ds, output_pairs *op, int threshold)
{
    // TODO implement a quick `find_pairs_quick_indexes` procedure using
    // `get_term_indexes`.
}
