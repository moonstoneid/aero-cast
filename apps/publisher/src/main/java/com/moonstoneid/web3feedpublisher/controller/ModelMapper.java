package com.moonstoneid.web3feedpublisher.controller;

import java.util.ArrayList;
import java.util.List;

import com.moonstoneid.web3feedpublisher.controller.model.EntryVM;
import com.moonstoneid.web3feedpublisher.model.Entry;

public final class ModelMapper {

    private ModelMapper() {}

    public static List<EntryVM> toViewModel(List<Entry> entries) {
        List<EntryVM> entriesVM = new ArrayList<>();
        for (Entry entry : entries) {
            entriesVM.add(toViewModel(entry));
        }
        return entriesVM;
    }

    public static EntryVM toViewModel(Entry entry) {
        EntryVM entryVM = new EntryVM();
        entryVM.setId(entry.getId());
        entryVM.setTitle(entry.getTitle());
        entryVM.setDescription(entry.getDescription());
        entryVM.setDate(entry.getDate());
        return entryVM;
    }

    public static Entry toModel(EntryVM entryVM) {
        Entry entry = new Entry();
        entry.setTitle(entryVM.getTitle());
        entry.setDescription(entryVM.getDescription());
        entry.setDate(entryVM.getDate());
        return entry;
    }

}
